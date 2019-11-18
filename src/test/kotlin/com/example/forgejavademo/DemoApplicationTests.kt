package com.example.forgejavademo

import forge_abi.Delegate
import forge_abi.Rpc
import forge_abi.Type
import io.arcblock.forge.ForgeSDK
import io.arcblock.forge.did.DIDGenerator
import io.arcblock.forge.extension.decodeB64Url
import io.arcblock.forge.extension.encodeB58
import io.grpc.stub.StreamObserver
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import java.math.BigDecimal
import java.math.BigInteger

@RunWith(JUnit4::class)

class DemoApplicationTests {

	@Test
	fun decodeTx(){
		var txB=
						"CiN6MVlZR3FhVWRLNzJmeG5DZDJ2dEh2RUtWcktreURVcjFBMxCyhKTy4y0aD3ppbmMtMjAxOS0wNS0xNyIgQgsfwdwZL2Wg1zmtkqjthddG_PxnysvIFsMfyQW0hgFqQNLrh3z2Ub4EuKeC3soZrfmpgNrYqQju-O9AK7bDxYkt4dPnBh5Gy8B-3jIimrFDLxyF3GYNM3X-o__SKBI34QV6cgoNZmc6dDpkZWxlZ2F0ZRJhCiV6MmJNdXdwMnJ0QVh5bXNNclJKc01XS2pmV3JNQUpyVTk2aUJGEiN6MVlZR3FhVWRLNzJmeG5DZDJ2dEh2RUtWcktreURVcjFBMxoTCg9mZzp0OnNldHVwX3N3YXASAA".decodeB64Url()
		var tx = Type.Transaction.parseFrom(txB)
		println("Tx:$tx")
		val itx = Delegate.DelegateTx.parseFrom(tx.itx.value)
		println("Itx:$itx")



	}

	@Test
	fun randomWallet(){
		val w = DIDGenerator.randomWallet()
		println("address:${w.address}")
		println("pk:${w.pkBase58()}")
		println("sk:${w.sk.encodeB58()}")
	}

	@Test
	fun contextLoads() {
		val forge = ForgeSDK.connect("localhost",28212)
		val accountRequest = forge.getAccountState(object : StreamObserver<Rpc.ResponseGetAccountState> {
			override fun onNext(value: Rpc.ResponseGetAccountState?) {
				value?.state?.balance?.toByteArray()
				println("\nAccountState balance:\n${unsigned(BigInteger(value?.state?.balance?.value?.toByteArray()))}")

			}
			override fun onError(t: Throwable?) {}
			override fun onCompleted() {}
		})
		accountRequest.onNext(Rpc.RequestGetAccountState.newBuilder().setAddress("z11DxjNXtMAYB6nFMyNGZoxXcXggkX4Erw87").build())
		Thread.sleep(5000)

	}

	fun unsigned(b: BigInteger): BigInteger {
		if (b.signum() >= 0) {
			return b
		}
		val a1 = b.toByteArray()
		val a2 = ByteArray(a1.size + 1)
		a2[0] = 0
		System.arraycopy(a1, 0, a2, 1, a1.size)
		return BigInteger(a2)
	}

}
