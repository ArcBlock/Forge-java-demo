//package com.example.forgejavademo;
//
//import com.google.common.io.BaseEncoding;
//import com.google.protobuf.ByteString;
//
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.junit.runners.JUnit4;
//
//import forge_abi.Rpc;
//import forge_abi.Type;
//import io.arcblock.forge.ForgeSDK;
//import io.arcblock.forge.WalletUtils;
//import io.arcblock.forge.did.DIDGenerator;
//import io.arcblock.forge.did.KeyType;
//import io.arcblock.forge.did.WalletInfo;
//import io.arcblock.forge.sign.Signer;
//
///**
// * Author       :
// * Time         : 2019-10-29
// * Edited By    :
// * Edited Time  :
// * Description  :
// **/
//
//@RunWith(JUnit4.class)
//public class TestTransfer {
//  @Test
//  public void test(){
//    ForgeSDK forge = ForgeSDK.Companion.connect("116.62.158.72",28210);
////    ForgeSDK forge = ForgeSDK.Companion.connect("localhost",28210);
//    Rpc.ResponseSendTx sendResponse = null;
//    ByteString sk = ByteString.copyFrom(BaseEncoding
//      .base64Url().decode("v9_-BPhwLUgz6-KI6vD0xWgtwicqMGjjT4Y7qjQHO2rkTaCRuEc19zPVCp0MpCoD4EZCh-3fnKoBkRc8ItO-zg"));
//    ByteString pk = ByteString.copyFrom(BaseEncoding.base64Url().decode("5E2gkbhHNfcz1QqdDKQqA-BGQoft35yqAZEXPCLTvs4"));
//    //Type.WalletInfo walletInfo = Type.WalletInfo.newBuilder().setSk(sk).setPk(pk).setAddress("z119LLxgzRqMd3W9LK92TyW6Y9q8kF1nu2dy").build();
//
//    //WalletInfo wallet = DIDGenerator.INSTANCE.randomWallet();
////    WalletInfo wallet = new WalletInfo(DIDGenerator.INSTANCE.sk2did(sk.toByteArray()).substring(8),sk.toByteArray(),
////      WalletUtils.INSTANCE.sk2pk(KeyType.ED25519,
////      sk.toByteArray()));
////
////    Type.WalletInfo walletInfo =
////      Type.WalletInfo.newBuilder().setSk(sk).setPk(pk).setAddress(wallet.getAddress()).build();
////    sendResponse = forge.poke(walletInfo);
////
//    System.out.println("response:"+sendResponse.toString());
//  }
//}
