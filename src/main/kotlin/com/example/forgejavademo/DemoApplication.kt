package com.example.forgejavademo

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import com.google.common.io.BaseEncoding
import forge_abi.Rpc.*
import forge_abi.Type
import io.arcblock.forge.*

import io.arcblock.forge.did.WalletInfo

import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*

import com.google.protobuf.ByteString
import io.arcblock.forge.did.DIDGenerator
import io.arcblock.forge.did.KeyType
import io.arcblock.forge.extension.address
import io.arcblock.forge.extension.decodeB58
import io.arcblock.forge.extension.encodeB64Url
import io.arcblock.forge.extension.toByteString
import io.arcblock.forge.sign.Signer
import org.springframework.context.annotation.Configuration
import io.github.logger.controller.aspect.GenericControllerAspect
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean


@SpringBootApplication
class DemoApplication

fun main(args: Array<String>) {
	runApplication<DemoApplication>(*args)
}


@Configuration
class InitConfig{
	@Bean
	fun genericControllerAspect(): GenericControllerAspect {
		return GenericControllerAspect()
	}
}





@Component
class InitLine : CommandLineRunner {
	var logger = LoggerFactory.getLogger("Forge Init")
	@Autowired lateinit var forge: ForgeSDKComponent

	override fun run(vararg args: String?) {
		// Connect forge chain node

//		forge.sdk = ForgeSDK.connect("localhost", port.toInt())
		forge.sdk!!.declare("application", forge.wallet)



		// Query Chain info
//		val chainInfo = forge.getChainInfo().info
//		logger.info("\nchain info:\n$chainInfo\n\n\n")
//
//		val sk = BaseEncoding
//			.base64Url().decode("v9_-BPhwLUgz6-KI6vD0xWgtwicqMGjjT4Y7qjQHO2rkTaCRuEc19zPVCp0MpCoD4EZCh-3fnKoBkRc8ItO-zg")
//		//val pk = ByteString.copyFrom(BaseEncoding.base64Url().decode("5E2gkbhHNfcz1QqdDKQqA-BGQoft35yqAZEXPCLTvs4"))
//		//Type.WalletInfo walletInfo = Type.WalletInfo.newBuilder().setSk(sk).setPk(pk).setAddress("z119LLxgzRqMd3W9LK92TyW6Y9q8kF1nu2dy").build();
//		logger.info("pk${WalletUtils.sk2pk(KeyType.ED25519,sk).encodeB64Url()}")
//
//		//WalletInfo wallet = DIDGenerator.INSTANCE.randomWallet();
////    val wallet = WalletInfo(DIDGenerator.sk2did(sk).substring(8), sk,
////      WalletUtils.sk2pk(KeyType.ED25519,
////        sk))
//		val content = "abcdsfsdfsdf"
//		//val pkS = WalletUtils.sk2pk(KeyType.ED25519,sk).encodeB64Url()
//		val signature =  Signer.sign(KeyType.ED25519,content.toByteArray(),sk)
//		val rst = Signer.verify(KeyType.ED25519,content.toByteArray(), WalletUtils.sk2pk(KeyType.ED25519,sk), signature)
//
//		val walletInfo = Type.WalletInfo.newBuilder()
//			.setSk(ByteString.copyFrom(sk))
//			.setPk(ByteString.copyFrom(WalletUtils.sk2pk(KeyType.ED25519,sk)))
//			.setAddress(DIDGenerator.sk2did(sk).address())
//			.build()
//		val sendResponse = forge.poke(walletInfo)
//		System.out.println("wallet address:"  +"   $rst")
//		System.out.println("response:" + sendResponse.toString())
//
//
//		// Query forge current state
//		val forgeState = forge.getForgeState(RequestGetForgeState.getDefaultInstance()).state
//
//		// create two wallet
//		val alice = forge.createWallet()
//		val bob = forge.createWallet()
//		forge.poke(alice)
//		forge.poke(bob)
//		logger.info("\n\n\n Alice\n $alice\n\n\n")
//		Thread.sleep(5000) //wait for block to commit
//
//		// create a stream to listen account state
//		val accountRequest = forge.getAccountState(object : StreamObserver<ResponseGetAccountState> {
//			override fun onNext(value: ResponseGetAccountState?) {
//				logger.info("\nAccountState balance:\n${BigInteger(value?.state?.balance?.value?.toByteArray())}")
//			}
//			override fun onError(t: Throwable?) {}
//			override fun onCompleted() {}
//		})
//		accountRequest.onNext(RequestGetAccountState.newBuilder().setAddress(alice.address).build())
//		val hashQuery = forge.getTx(object : StreamObserver<ResponseGetTx>{
//			override fun onError(t: Throwable?) {
//				logger.error(t?.message)
//			} override fun onCompleted() {}
//			override fun onNext(value: ResponseGetTx?) {
//				logger.info("\n\n\nHash State:\n$value")
//				logger.info("\nCode::${value?.code}")
//			}
//		})
//
//		// create a Poke transaction and send
////		val tx = TransactionFactory.unsignPoke("zzzzzzzzzzzzzzzzzz",chainInfo.network, WalletInfo(alice.wallet)).signTx(alice.wallet.sk.toByteArray())
////		val response = forge.sendTx(tx)
////		logger.info("\npoke:\n$response")
////		hashQuery.onNext(Rpc.RequestGetTx.newBuilder().setHash(response.hash).build())
//
//		forge.poke(alice)
//		Thread.sleep(5000) //wait for block to commit
//		//Query account balance and transaction hash
//		accountRequest.onNext(RequestGetAccountState.newBuilder().setAddress(alice.address).build())
////		hashQuery.onNext(Rpc.RequestGetTx.newBuilder().setHash(response.hash).build())
//
//
//
//
//		//create a transfer tx and send
//		forge.transfer(alice, bob, BigDecimal("2E18").toBigInteger())
//
//		Thread.sleep(5000) //wait for block to commit
//		//Query account balance again
//		accountRequest.onNext(RequestGetAccountState.newBuilder().setAddress(alice.address).build())
//
//
//		//Create stream listener of asset
//		val queryAsset = forge.getAssetState(object : StreamObserver<ResponseGetAssetState>{
//			override fun onNext(value: ResponseGetAssetState?) {
//				logger.info("\n@@@@@@@ Asset @@@@@@@")
//				logger.info("\n@@@@@@@ Asset @@@@@@@\n${value}")
//			}
//			override fun onError(t: Throwable?) {}
//			override fun onCompleted() {}
//		})
//
//		//create simple asset
//		val ( assetResponse, assetAddress) = forge.createAsset("string","abcdefg-${UUID.randomUUID().toString()}".toByteArray(),"MonikerCan'tBeEmpty",alice)
//		logger.info("\n\n\n@@@@@@@ Create Asset @@@@@@@ $assetAddress")
//		logger.info("\n $assetResponse")
//		Thread.sleep(5000) //wait for block to commit
//		queryAsset.onNext(RequestGetAssetState.newBuilder().setAddress(assetAddress)
//			.build())
//		Thread.sleep(1000) //wait for block to commit

//		forge.exchange(alice,bob, BigInteger.TEN,assetAddress)

	}
}