package com.example.forgejavademo

import io.arcblock.forge.ForgeSDK
import io.arcblock.forge.did.WalletInfo
import io.arcblock.forge.extension.decodeB58
import org.springframework.stereotype.Component

@Component
class ForgeSDKComponent {
	var sdk: ForgeSDK = ForgeSDK.connect("localhost", 28212)
	var wallet = WalletInfo(
					"z1RuF9Xa2AUTJ3VHZKy2xroKzphwWKsLrY6","zGsRPbaQEg198qrcczbmNsMDqujKnqzz1Zd9AhYtAKeAg".decodeB58(),"z1EF21mrPBwkwRGM88gq5hWstZQdA585bVU4QfrLC9HJfz".decodeB58())
}
