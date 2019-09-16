# Forge Java Demo
---------------------------------------------------------

# Spring install

```bash
brew install springboot
```

# create a new project

```bash
spring init --build=gradle --language=kotlin forge-java-demo
```

# Start a local forge

you have to install [forge-cli](https://docs.arcblock.io/forge/latest/tools/forge_cli.html) and setup a local chain.

```bash
forge start
```

# Add dependencies

```gradle
 repositories {
    maven { url "http://android-docs.arcblock.io/release" }
 }

 dependencies {
    implementation("io.arcblock.forge:core:${forge_version}")
    implementation("io.grpc:grpc-netty:1.20.0")
 }
```

# Connect to forge

```kotlin
    val forge = ForgeSDK.connect("localhost",28210)
```

# Get ChainInfo and ChainState

```kotlin
val chainInfo = forge.getChainInfo(RequestGetChainInfo.getDefaultInstance()).info
		val forgeState = forge.getForgeState(RequestGetForgeState.getDefaultInstance()).state
```

# Create Wallet

```kotlin
val alice = forge.createWallet("alice", "123qweASD").wallet
val bob = forge.createWallet("bobbb", "123qweASD").wallet
```

# Poke

send a poke transaction to get 25TBA for alice

```kotlin
forge.poke(alice, forge.getForgeState().state.pokeConfig)
```

# Transfer

```kotlin
val response = forge.transfer(alice, bob, BigInteger.ONE)
```

# Query the result at explorer

open [http://localhost:8210/node/explorer/txs](http://localhost:8210/node/explorer/txs) to check transactions
