package com.example.forgejavademo

import io.arcblock.forge.ForgeSDK
import io.arcblock.forge.did.WalletInfo
import io.arcblock.forge.extension.decodeB58
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment


@Component
class ForgeSDKComponent {

//	@Autowired
//	lateinit var  env: Environment

	@Value("\${forge.tcp.port}")
	lateinit var port: String

	var sdk: ForgeSDK? = null
		get() {
			if(field == null) {
				field = ForgeSDK.connect("localhost", port.toInt())
			}
			return field
		}
	var wallet = WalletInfo(
					"z1RuF9Xa2AUTJ3VHZKy2xroKzphwWKsLrY6","zGsRPbaQEg198qrcczbmNsMDqujKnqzz1Zd9AhYtAKeAg".decodeB58(),"z1EF21mrPBwkwRGM88gq5hWstZQdA585bVU4QfrLC9HJfz".decodeB58())

}
