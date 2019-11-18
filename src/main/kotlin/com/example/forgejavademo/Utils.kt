package com.example.forgejavademo

import org.springframework.http.codec.HttpMessageEncoder
import sun.nio.cs.ext.DoubleByte
import java.net.*
import java.nio.charset.StandardCharsets
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
object Utils {

  fun didUrl(url: String, pkB58: String, addr: String): String{
    return URLEncoder.encode("https://abtwallet.io/i?action=requestAuth&url=$url", StandardCharsets.UTF_8.toString())
  }
  /**
   * Returns this host's non-loopback IPv4 addresses.
   *
   * @return
   * @throws SocketException
   */
  @Throws(SocketException::class)
  fun getInet4Addresses(): List<Inet4Address> {
    val ret = ArrayList<Inet4Address>()

    val nets = NetworkInterface.getNetworkInterfaces()
    for (netint in Collections.list(nets)) {
      val inetAddresses = netint.getInetAddresses()
      for (inetAddress in Collections.list(inetAddresses)) {
        if (inetAddress is Inet4Address && !inetAddress.isLoopbackAddress() && inetAddress.hostAddress != "127.0.0.1") {
          ret.add(inetAddress as Inet4Address)
        }
      }
    }

    return ret
  }

  /**
   * Returns this host's first non-loopback IPv4 address string in textual
   * representation.
   *
   * @return
   * @throws SocketException
   */
  @Throws(SocketException::class)
  fun getHost4Address(): String {
    val inet4 = getInet4Addresses()
    return if (!inet4.isEmpty())
      inet4[0].hostAddress
    else
      ""
  }
}