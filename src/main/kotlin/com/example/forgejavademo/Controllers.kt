package com.example.forgejavademo

import com.example.forgejavademo.db.TokenData
import com.example.forgejavademo.db.TokenReposity
import com.example.forgejavademo.db.UserData
import com.example.forgejavademo.db.UserReposity
import com.google.gson.Gson
import com.google.gson.JsonObject
import forge_abi.Enum
import forge_abi.Rpc
import forge_abi.TraceType
import forge_abi.Type
import io.arcblock.forge.TransactionFactory
import io.arcblock.forge.did.DidAuthUtils
import io.arcblock.forge.did.HashType
import io.arcblock.forge.did.WalletInfo
import io.arcblock.forge.did.bean.*
import io.arcblock.forge.extension.*
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys.secretKeyFor
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.math.BigDecimal
import java.util.*


/**
 *
 *     █████╗ ██████╗  ██████╗██████╗ ██╗      ██████╗  ██████╗██╗  ██╗
 *    ██╔══██╗██╔══██╗██╔════╝██╔══██╗██║     ██╔═══██╗██╔════╝██║ ██╔╝
 *    ███████║██████╔╝██║     ██████╔╝██║     ██║   ██║██║     █████╔╝
 *    ██╔══██║██╔══██╗██║     ██╔══██╗██║     ██║   ██║██║     ██╔═██╗
 *    ██║  ██║██║  ██║╚██████╗██████╔╝███████╗╚██████╔╝╚██████╗██║  ██╗
 *    ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝╚═════╝ ╚══════╝ ╚═════╝  ╚═════╝╚═╝  ╚═╝
 * Author       : shan@arcblock.io
 * Time         : 2019-11-04
 * Edited By    :
 * Edited Time  :
 * Description  :
 **/

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = ["*"])
//@Logging
class Controllers(private val tokenRepo: TokenReposity, private val userRepo: UserReposity) {
  final val logger = LoggerFactory.getLogger("api")
  var key = secretKeyFor(SignatureAlgorithm.HS256)

  @Autowired
  lateinit var forge: ForgeSDKComponent

  @Value("\${chainHost}")
  lateinit var forgeChainHost: String

  var ip = Utils.getHost4Address()
  var appInfo = lazy {
    AppInfo().let {
      it.chainHost = forgeChainHost
      it.publisher = forge.wallet.address.did()
      it.name = "Forge Java Demo"
      it
    }
  }


  @Value("\${server.port}")
  lateinit var port: String

  init {
    logger.info("currenIp: $ip")
  }


  @RequestMapping("/session", method = [RequestMethod.GET, RequestMethod.POST])
  @ResponseBody
  fun getSesstion(@RequestHeader("Authorization") token: String?): String {
    if (token.isNullOrEmpty() || token?.contains("Bearer") != true || token.removePrefix("Bearer").trim().isEmpty()) {
      return "{}"
    } else {
      val jwt = token.removePrefix("Bearer")
        .trim()
      val claim = Jwts.parser()
        .setSigningKey(key)
        .parseClaimsJws(jwt)
        .body
      var json = JsonObject()
      var user = JsonObject()

      claim.keys.forEach { user.addProperty(it, claim[it].toString()) }
      json.add("user", user)
      logger.info("session:$json")
      return json.toString()
    }
  }

  @RequestMapping("/payments", method = [RequestMethod.GET])
  @ResponseBody
  fun payment(@RequestHeader("Authorization") token: String?): String {
    if (token.isNullOrEmpty() || token?.contains("Bearer") != true || token.removePrefix("Bearer").trim().isEmpty()) {
      return "{}"
    } else {
      val jwt = token.removePrefix("Bearer")
        .trim()
      val claim = Jwts.parser()
        .setSigningKey(key)
        .parseClaimsJws(jwt)
        .body
      val did = claim["did"].toString()
      val response = forge.sdk!!.listTransactions(Rpc.RequestListTransactions.newBuilder()
        .setAddressFilter(TraceType.AddressFilter.newBuilder().setSender(did.address()).setReceiver(forge.wallet.address).build())
        .build())
        .transactionsList.firstOrNull { it.code == Enum.StatusCode.ok }
      return if (response == null) "null" else JsonObject().apply { this.addProperty("hash", response.hash) }.toString()
    }

    return "{}"
  }


  @RequestMapping("/did/{act}/{any}", method = [RequestMethod.GET])
  @ResponseBody
  fun getAuth(@PathVariable("act") act: String, @PathVariable("any") any: String, @RequestParam("_t_") t: String): String {
    val url = ServletUriComponentsBuilder.fromCurrentRequest()
      .build()
      .toUri()
      .toString()
      .replace("localhost", ip)
      .replace("127.0.0.1", ip)
    val token = tokenRepo.findById(t)
    val jwt = DidAuthUtils.createDidAuthToken(arrayOf(AuthPrincipalClaim(null, if (token.isPresent) token.get().did?.did() else null, "")),
            appInfo
              .value
            , System.currentTimeMillis() / 1000, wallet = forge.wallet, url = url, others = null)
    return JsonObject().let {
      it.addProperty("appPk", forge.wallet.pkBase58())
      it.addProperty("authInfo", jwt)
      it
    }
      .toString()
  }

  @RequestMapping("/did/{act}/auth", method = [RequestMethod.POST])
  @ResponseBody
  fun postAuth(@PathVariable("act") act: String, @RequestParam("_t_") t: String, @RequestBody body: DidRequestBody): String {
    val url = ServletUriComponentsBuilder.fromCurrentRequest()
      .build()
      .toUri()
      .toString()
      .replace("localhost", ip)
      .replace("127.0.0.1", ip)
      .replace("/auth", "/auth/rst")
    val jwt = DidAuthUtils.parseJWT(body.userInfo)
    val claims = when (act) {
      "payment" -> {
        logger.info("\n\njwt:${jwt.iss}   ${body.userPk}\n\n")
        val unsignedTx = TransactionFactory.unsignTransfer(forge.sdk!!.chainInfo.value.network, jwt.iss.address() ?: "", body.userPk!!.decodeB58(), forge.wallet
          .address,
                token =
                BigDecimal("5E18").toBigInteger().unSign())
        arrayOf(SignatureClaim(meta = null, description = "Please pay 5 TBA", typeUrl = "utf-8", origin = unsignedTx.toByteArray().encodeB58(), data =
        unsignedTx.toByteArray().hash(HashType.SHA3).encodeB58(), sig = ""))
      }
      "checkin" -> {
        val unsignedTx = TransactionFactory.unsignPoke(forge.sdk!!.chainInfo.value.network, WalletInfo(jwt.iss.address(), body.userPk!!.decodeB58(), ByteArray
        (0)))
        arrayOf(SignatureClaim(null, "", origin = unsignedTx.toByteArray().encodeB58(), data = unsignedTx.toByteArray().hash(HashType.SHA3).encodeB58(),
                description = "CheckIn and Get 25TBA"))
      }
      else -> arrayOf(ProfileClaim(null, arrayListOf("fullname", "email", "phone"), "choose a profile", ""))
    }

    val ret = DidAuthUtils.createDidAuthToken(claims, appInfo.value
            , System.currentTimeMillis() / 1000, wallet = forge.wallet, url = url, others = null)
    return JsonObject().let {
      it.addProperty("appPk", forge.wallet.pkBase58())
      it.addProperty("authInfo", ret)
      it
    }
      .toString()
  }

  @RequestMapping("/did/{act}/auth/rst", method = [RequestMethod.POST])
  @ResponseBody
  fun postAuthRst(@PathVariable("act") act: String, @RequestParam("_t_") t: String, @RequestBody body: DidRequestBody): String {

    val url = ServletUriComponentsBuilder.fromCurrentRequest()
      .build()
      .toUri()
      .toString()
      .replace("localhost", ip)
    val data = DidAuthUtils.parseJWT(body.userInfo)
    val claim = data.requestedClaims.firstOrNull()
      ?.asJsonObject
    var othersJsonObject = if (claim == null) {
      JsonObject().apply { this.addProperty("error", "provide data emptry or error") }
    } else {
      logger.info("\n\n\nuser: $claim\n\n\n")
      when (claim["type"].asString.toLowerCase()) {
        ClaimType.PROFILE.name.toLowerCase() -> {
          var user = userRepo.save(UserData(claim["fullname"]?.asString ?: "", claim["phone"]?.asString ?: "", claim["email"]?.asString ?: "", data.iss))
          val jws = Jwts.builder()
            .setClaims(mapOf(
                    "id" to user.id,
                    "name" to user.name,
                    "mobile" to user.mobile,
                    "email" to user.email,
                    "did" to user.did
            ))
            .signWith(key)
            .compact()
          var token = tokenRepo.findById(t)
            .get()
            .apply {
              this.status = "succeed"
              this.sessionToken = jws
            }
          tokenRepo.save(token)
          JsonObject().apply { this.addProperty("status", "ok") }
        }
        ClaimType.SIGNATURE.name.toLowerCase() -> {
          val tx = Type.Transaction.parseFrom(claim["origin"].asString.decodeB58())
            .toBuilder()
            .setSignature(claim["sig"].asString.decodeB58().toByteString
            ())
            .build()
          val resp = forge.sdk!!.sendTx(tx)
          JsonObject().apply {
            this.addProperty("status", "ok")
            this.add("response", JsonObject().apply {
              this.addProperty("hash", resp.hash)
              this.addProperty("tx", tx.toByteArray().encodeB58())
            })
          }
        }
        else -> JsonObject().apply { this.addProperty("status", "ok") }
      }
    }
    val ret = DidAuthUtils.createDidAuthToken(arrayOf(), appInfo.value
            , System.currentTimeMillis() / 1000, wallet = forge.wallet, url = url, others = othersJsonObject)
    return JsonObject().let {
      it.addProperty("appPk", forge.wallet.pkBase58())
      it.addProperty("authInfo", ret)
      it
    }
      .toString()

  }


  @RequestMapping("/did/{act}/token", method = [RequestMethod.GET])
  @ResponseBody
  fun token(@PathVariable("act") act: String, @RequestParam("_t_") t: String?): String {
    var json = JsonObject()
    val bytes = ByteArray(10)
//      sun.security.provider.SecureRandom()
//        .engineNextBytes(bytes)
    Random().nextBytes(bytes)
    val token = bytes.encodeB16()
    tokenRepo.save(TokenData(token, "created"))
    json.addProperty("token", token)
    json.addProperty("url", Utils.didUrl("http://$ip:$port/api/did/$act/auth?_t_=$token", forge.wallet.pk.encodeB58(), forge.wallet.address))
    return json.toString()
  }

  @RequestMapping("/did/{act}/status", method = [RequestMethod.GET])
  @ResponseBody
  fun status(@PathVariable("act") act: String, @RequestParam("_t_") t: String): String {
    val query = tokenRepo.findById(t)

    if (query.isPresent) {
      val ret = Gson().toJson(query.get())
      return ret
    } else {
      return "{}"
    }
  }

  @RequestMapping("/did/timeout", method = [RequestMethod.GET])
  @ResponseBody
  fun timeout(@RequestParam("_t_") t: String): String {
    tokenRepo.deleteById(t)
    return "{\"msg\":\"token mardked as expired\"}"
  }


}