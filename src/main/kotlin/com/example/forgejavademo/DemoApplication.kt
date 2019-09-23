package com.example.forgejavademo

import forge_abi.Enum.HashType.sha3
import forge_abi.Enum.KeyType.ed25519
import forge_abi.Enum.RoleType.role_account
import forge_abi.Rpc
import forge_abi.Rpc.RequestGetAccountState
import forge_abi.Rpc.RequestGetChainInfo
import forge_abi.Rpc.RequestGetForgeState
import forge_abi.Rpc.ResponseGetAccountState
import forge_abi.Type
import io.arcblock.forge.ForgeSDK
import io.arcblock.forge.TransactionFactory
import io.arcblock.forge.did.WalletInfo
import io.arcblock.forge.sendTx
import io.arcblock.forge.signTx
import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.BigInteger

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
			override fun onError(t: Throwable?) {} override fun onCompleted() {}
			override fun onNext(value: Rpc.ResponseGetTx?) {
				logger.info("\nHash State:\n${value?.info}")
			}
		})

		// create a Poke transaction and send
		val tx = TransactionFactory.unsignPoke(forgeState.pokeConfig.address,chainInfo.network, WalletInfo(alice.wallet)).signTx(alice.wallet.sk.toByteArray())
		val response = forge.sendTx(tx)
		logger.info("\npoke:\n$response")

		Thread.sleep(5000) //wait for block to commit
		//Query account balance and transaction hash
		accountRequest.onNext(RequestGetAccountState.newBuilder().setAddress(alice.wallet.address).build())
		hashQuery.onNext(Rpc.RequestGetTx.newBuilder().setHash(response.hash).build())


		//create a transfer tx and send
		forge.transfer(alice.wallet, bob.wallet, BigDecimal("2E18").toBigInteger())

		Thread.sleep(5000) //wait for block to commit
		//Query account balance again
		accountRequest.onNext(RequestGetAccountState.newBuilder().setAddress(alice.wallet.address).build())

	}
}