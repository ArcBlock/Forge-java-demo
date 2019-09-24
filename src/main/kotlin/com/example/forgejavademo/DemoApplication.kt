package com.example.forgejavademo

import forge_abi.Enum
import forge_abi.Enum.HashType.sha3
import forge_abi.Enum.KeyType.ed25519
import forge_abi.Enum.RoleType.role_account
import forge_abi.Rpc
import forge_abi.Rpc.RequestGetAccountState
import forge_abi.Rpc.RequestGetChainInfo
import forge_abi.Rpc.RequestGetForgeState
import forge_abi.Rpc.ResponseGetAccountState
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

@SpringBootApplication
class DemoApplication

fun main(args: Array<String>) {
	runApplication<DemoApplication>(*args)
}

@Component
class InitLine : CommandLineRunner {
	var logger = LoggerFactory.getLogger("Forge Init")
	override fun run(vararg args: String?) {
		// Connect forge chain node
		val forge = ForgeSDK.connect("localhost",28210)
		// Query Chain info
		val chainInfo = forge.getChainInfo(RequestGetChainInfo.getDefaultInstance()).info
		logger.info("\nchain info:\n$chainInfo\n\n\n")

		// Query forge current state
		val forgeState = forge.getForgeState(RequestGetForgeState.getDefaultInstance()).state

		// create two wallet
		val alice = forge.createWallet("alice","123qweASD")
		val bob = forge.createWallet("bobbb","123qweASD")
		logger.info("\n\n\n Alice\n $alice\n\n\n")
		Thread.sleep(5000) //wait for block to commit

		// create a stream to listen account state
		val accountRequest = forge.getAccountState(object : StreamObserver<ResponseGetAccountState> {
			override fun onNext(value: ResponseGetAccountState?) {
				logger.info("\nAccountState balance:\n${BigInteger(value?.state?.balance?.value?.toByteArray())}")
			}
			override fun onError(t: Throwable?) {}
			override fun onCompleted() {}
		})
		accountRequest.onNext(RequestGetAccountState.newBuilder().setAddress(alice.wallet.address).build())
		val hashQuery = forge.getTx(object : StreamObserver<Rpc.ResponseGetTx>{
			override fun onError(t: Throwable?) {
				logger.error(t?.message)
			} override fun onCompleted() {}
			override fun onNext(value: Rpc.ResponseGetTx?) {
				logger.info("\n\n\nHash State:\n$value")
				logger.info("\nCode::${value?.code}")
			}
		})

		// create a Poke transaction and send
		val tx = TransactionFactory.unsignPoke(forgeState.pokeConfig.address,chainInfo.network, WalletInfo(alice.wallet)).signTx(alice.wallet.sk.toByteArray())
		val response = forge.sendTx(tx)
		logger.info("\npoke:\n$response")
		hashQuery.onNext(Rpc.RequestGetTx.newBuilder().setHash(response.hash).build())


		Thread.sleep(5000) //wait for block to commit
		//Query account balance and transaction hash
		accountRequest.onNext(RequestGetAccountState.newBuilder().setAddress(alice.wallet.address).build())
		hashQuery.onNext(Rpc.RequestGetTx.newBuilder().setHash(response.hash).build())


		//create a transfer tx and send
		forge.transfer(alice.wallet, bob.wallet, BigDecimal("2E18").toBigInteger())

		Thread.sleep(5000) //wait for block to commit
		//Query account balance again
		accountRequest.onNext(RequestGetAccountState.newBuilder().setAddress(alice.wallet.address).build())


		//Create stream listener of asset
		val queryAsset = forge.getAssetState(object : StreamObserver<Rpc.ResponseGetAssetState>{
			override fun onNext(value: Rpc.ResponseGetAssetState?) {
				logger.info("\n@@@@@@@ Asset @@@@@@@")
				logger.info("\n@@@@@@@ Asset @@@@@@@\n${value}")
			}
			override fun onError(t: Throwable?) {}
			override fun onCompleted() {}
		})

		//create simple asset
		val ( assetResponse, assetAddress) = forge.createAsset("string","abcdefg-${UUID.randomUUID().toString()}".toByteArray(),"MonikerCan'tBeEmpty",alice.wallet)
		logger.info("\n\n\n@@@@@@@ Create Asset @@@@@@@ $assetAddress")
		logger.info("\n $assetResponse")
		Thread.sleep(5000) //wait for block to commit
		queryAsset.onNext(Rpc.RequestGetAssetState.newBuilder().setAddress(assetAddress)
			.build())
		Thread.sleep(1000) //wait for block to commit

//		forge.exchange(alice,bob, BigInteger.TEN,assetAddress)

	}
}