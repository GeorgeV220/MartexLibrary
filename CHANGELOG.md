# [11.6.0-beta.11](https://github.com/GeorgeV220/MartexLibrary/compare/v11.6.0-beta.10...v11.6.0-beta.11) (2023-07-06)


### Features

* Add EntityManager interface and Entity class ([a5c9f5a](https://github.com/GeorgeV220/MartexLibrary/commit/a5c9f5a0b002e1a0dcb042643aa7bef5563bfbd9))
* **database:** implement EntityManagerImpl class for managing EntityImpl objects ([4531f35](https://github.com/GeorgeV220/MartexLibrary/commit/4531f352f95eb41976bca67ea49ab2e93dcb3f47))

# [11.6.0-beta.10](https://github.com/GeorgeV220/MartexLibrary/compare/v11.6.0-beta.9...v11.6.0-beta.10) (2023-07-05)


### Features

* **Entity:** Add getCustomData method ([8a129af](https://github.com/GeorgeV220/MartexLibrary/commit/8a129af5055e558d896b3914c7dd1342e4eb20c1))
* Remove entity from loadedEntities when deleting it ([c906181](https://github.com/GeorgeV220/MartexLibrary/commit/c906181da811842c14224b482d78aa119bec009f))

# [11.6.0-beta.9](https://github.com/GeorgeV220/MartexLibrary/compare/v11.6.0-beta.8...v11.6.0-beta.9) (2023-07-05)


### Features

* **task:** Add serialVersionUID to serializable classes ([a64f4be](https://github.com/GeorgeV220/MartexLibrary/commit/a64f4be6f07fa590c23343d4231f0ea0c2c22d31))

# [11.6.0-beta.8](https://github.com/GeorgeV220/MartexLibrary/compare/v11.6.0-beta.7...v11.6.0-beta.8) (2023-07-04)


### Bug Fixes

* **EntityManager:** Fix saveAll() method to correctly iterate over loadedEntities ([2839b06](https://github.com/GeorgeV220/MartexLibrary/commit/2839b063902c6942d295002c8aeb1926fccba58b))

# [11.6.0-beta.7](https://github.com/GeorgeV220/MartexLibrary/compare/v11.6.0-beta.6...v11.6.0-beta.7) (2023-07-04)


### Bug Fixes

* Change parameter type in placeholderAPI method from CommandSender to ServerOperator ([8fd4e71](https://github.com/GeorgeV220/MartexLibrary/commit/8fd4e713d3828fc5ad46a03ab9675860d83a2e68))

# [11.6.0-beta.6](https://github.com/GeorgeV220/MartexLibrary/compare/v11.6.0-beta.5...v11.6.0-beta.6) (2023-07-03)


### Features

* Add ActionSerializer for Kryo serialization ([062ab97](https://github.com/GeorgeV220/MartexLibrary/commit/062ab97f79dd2f06f2f277c28f9b3cb5ad491530))

# [11.6.0-beta.5](https://github.com/GeorgeV220/MartexLibrary/compare/v11.6.0-beta.4...v11.6.0-beta.5) (2023-07-03)


### Bug Fixes

* Update List to ArrayList in ItemCommandSerializer ([6daaf5a](https://github.com/GeorgeV220/MartexLibrary/commit/6daaf5ab2fb371c9cdbd5cf9ccce1bc860858eca))

# [11.6.0-beta.4](https://github.com/GeorgeV220/MartexLibrary/compare/v11.6.0-beta.3...v11.6.0-beta.4) (2023-07-03)


### Features

* Add 'com.esotericsoftware:kryo:5.5.0' as a compileOnly dependency ([1857292](https://github.com/GeorgeV220/MartexLibrary/commit/185729202df6d4ef92fd35b42141ab0e0506cc5e))
* Add serializers for ItemCommand, ItemStack, and Material ([10f30ab](https://github.com/GeorgeV220/MartexLibrary/commit/10f30aba8cd0c833fff7917ec311d1e1a2f24718))
* **inventory:** Add ItemCommandSerializer for ItemBuilder ([1c8f1d3](https://github.com/GeorgeV220/MartexLibrary/commit/1c8f1d34b19215a6a9cf3aec37614d5e407af80d))

# [11.6.0-beta.3](https://github.com/GeorgeV220/MartexLibrary/compare/v11.6.0-beta.2...v11.6.0-beta.3) (2023-07-03)


### Features

* **KryoUtils:** Add new methods ([4a42228](https://github.com/GeorgeV220/MartexLibrary/commit/4a42228f6b393d341b9b5821c0fe9d4ff23f068a))

# [11.6.0-beta.2](https://github.com/GeorgeV220/MartexLibrary/compare/v11.6.0-beta.1...v11.6.0-beta.2) (2023-07-02)


### Features

* **inventory:** Add Kryo enablement to InventoryRegistrar ([b1a33b4](https://github.com/GeorgeV220/MartexLibrary/commit/b1a33b42450679c7effccc02ed7ddaed45e545ef))

# [11.6.0-beta.1](https://github.com/GeorgeV220/MartexLibrary/compare/v11.5.0...v11.6.0-beta.1) (2023-07-02)


### Features

* Add KryoUtils class for serializing and deserializing objects ([28bde30](https://github.com/GeorgeV220/MartexLibrary/commit/28bde308727bf07be68b6b49ddf5cf4fde2cb084))
* Add support for Kryo serialization in ItemBuilder ([e1cecce](https://github.com/GeorgeV220/MartexLibrary/commit/e1cecce63ebd725f726d550b3098bdc5ae58682c))
* **build.gradle:** Add shadow configuration for dependencies ([3f14089](https://github.com/GeorgeV220/MartexLibrary/commit/3f14089568ea1e8dab30c7c8be68ecd89ac3f337))
* **inventory:** Add kryo enablement for PagedInventory ([8d5e7bb](https://github.com/GeorgeV220/MartexLibrary/commit/8d5e7bb3f285fcd599ea314511a4e76e31739888))

# [11.5.0](https://github.com/GeorgeV220/MartexLibrary/compare/v11.4.3...v11.5.0) (2023-07-02)


### Features

* **action:** add ability to add multiple actions to ActionManager ([58c47ec](https://github.com/GeorgeV220/MartexLibrary/commit/58c47ece18eb8184810445bc28ed990de417ade6))
* **inventory:** Change Action class to interface ([5919ef9](https://github.com/GeorgeV220/MartexLibrary/commit/5919ef942d6f3482c3730ad9c2f099aa1142ec51))
* **ItemBuilder:** Simplify code for building actions from config files ([9dd0d1e](https://github.com/GeorgeV220/MartexLibrary/commit/9dd0d1e9627ca18ae2255785341c7af7cf87efd8))

## [11.4.3](https://github.com/GeorgeV220/MartexLibrary/compare/v11.4.2...v11.4.3) (2023-07-01)


### Bug Fixes

* **ItemBuilder:** Update serialization method for colors and commands ([3c33de3](https://github.com/GeorgeV220/MartexLibrary/commit/3c33de3200979907f72d1c45f88c7a168f6538a4))

## [11.4.2](https://github.com/GeorgeV220/MartexLibrary/compare/v11.4.1...v11.4.2) (2023-07-01)


### Bug Fixes

* remove unnecessary exclusions and relocations in build.gradle 2 ([d65e567](https://github.com/GeorgeV220/MartexLibrary/commit/d65e567ac3afeab6f3b992882dd8d6d7b2169f5d))

## [11.4.1](https://github.com/GeorgeV220/MartexLibrary/compare/v11.4.0...v11.4.1) (2023-07-01)


### Bug Fixes

* remove unnecessary exclusions and relocations in build.gradle ([fa7b750](https://github.com/GeorgeV220/MartexLibrary/commit/fa7b750b158b7c16309731b2195e6a996faac827))

# [11.4.0](https://github.com/GeorgeV220/MartexLibrary/compare/v11.3.2...v11.4.0) (2023-07-01)


### Features

* **database:** Add delete method to EntityManager class ([f0614c5](https://github.com/GeorgeV220/MartexLibrary/commit/f0614c5a9f2f5bb319c90c2a8d9d06016f94d2b8))

## [11.3.2](https://github.com/GeorgeV220/MartexLibrary/compare/v11.3.1...v11.3.2) (2023-06-28)


### Bug Fixes

* Update dependencies and imports in bukkit build.gradle and inventory classes ([5b4be69](https://github.com/GeorgeV220/MartexLibrary/commit/5b4be69d5532f33c916c17a50aba349c2f554c78))

## [11.3.1](https://github.com/GeorgeV220/MartexLibrary/compare/v11.3.0...v11.3.1) (2023-06-27)


### Bug Fixes

* Removed JavaExtension from BukkitMinecraftUtils ([47218fc](https://github.com/GeorgeV220/MartexLibrary/commit/47218fc39205ae406cdbe01d23374901016a5862))

# [11.3.0](https://github.com/GeorgeV220/MartexLibrary/compare/v11.2.0...v11.3.0) (2023-06-11)


### Features

* **database:** Use setBytes() instead of setString() for BLOB columns ([fe7a1ca](https://github.com/GeorgeV220/MartexLibrary/commit/fe7a1caec4cab8fd80cc9bc18b69407c40305177))

# [11.2.0](https://github.com/GeorgeV220/MartexLibrary/compare/v11.1.1...v11.2.0) (2023-06-11)


### Features

* **database:** Use Base64 encoding instead of ISO_8859_1 in serialization methods ([cd62ba4](https://github.com/GeorgeV220/MartexLibrary/commit/cd62ba444170e862450d3bf1b428d9c5b6a057d4))

## [11.1.1](https://github.com/GeorgeV220/MartexLibrary/compare/v11.1.0...v11.1.1) (2023-06-11)


### Bug Fixes

* Relocate org.yaml.snakeyaml package into com.georgev22.library.snakeyaml ([37aafbd](https://github.com/GeorgeV220/MartexLibrary/commit/37aafbd8c7d6719847c06ec7a7b9e446bf94dde4))

# [11.1.0](https://github.com/GeorgeV220/API/compare/v11.0.0...v11.1.0) (2023-06-09)


### Features

* add SectionPathData class to handle comments and data in configuration sections ([7c93e78](https://github.com/GeorgeV220/API/commit/7c93e78bdd82abe6a9e0ecd572b9a3c3b408563b))
* **DatabaseWrapper:** Optimizations fixes and new methods ([e860949](https://github.com/GeorgeV220/API/commit/e860949b524f306b60dd2f45cbde25a5cee61592))
* **DatabaseWrapper:** Rewriting the entire class ([75c3e00](https://github.com/GeorgeV220/API/commit/75c3e008b542c27c1ae908eeedc0b348a743883c))
* **Yaml:** Upgrade to SnakeYaml 2.0 and updated from the latest bukkit ([abb61aa](https://github.com/GeorgeV220/API/commit/abb61aa29ce6b3235d05787b695e3315358ce38a))

# [11.0.0](https://github.com/GeorgeV220/API/compare/v10.2.3...v11.0.0) (2023-05-30)


### Features

* **maps:** Add methods to remove entries ([b8efaa3](https://github.com/GeorgeV220/API/commit/b8efaa3df5963851e206e3338ddddf2aa4c83171))
* Refactor EntityManager class and remove MongoDB references ([0281a4a](https://github.com/GeorgeV220/API/commit/0281a4add713d7cc4f90420334d60a30015b6795))
* **Utils:** add serializeObjectToBytes and deserializeObjectFromBytes methods ([73d67e2](https://github.com/GeorgeV220/API/commit/73d67e2c4cbf90aefda5191788870d60e08f667f))


### BREAKING CHANGES

* The EntityManager's `obj` variable, which accepts a vararg of objects, has been updated to only accept `File` or `DatabaseWrapper` objects.#

## [10.2.3](https://github.com/GeorgeV220/API/compare/v10.2.2...v10.2.3) (2023-05-29)


### Bug Fixes

* **database:** Removed entity_id from the entity contructor ([c85507c](https://github.com/GeorgeV220/API/commit/c85507c77020949397d0db24b4fd136215b847b1))

## [10.2.2](https://github.com/GeorgeV220/API/compare/v10.2.1...v10.2.2) (2023-05-29)


### Bug Fixes

* **database:** Use append instead of put to add loaded entities ([1b08f17](https://github.com/GeorgeV220/API/commit/1b08f1745df0b9920acb01404de48f5bd3610479))

## [10.2.1](https://github.com/GeorgeV220/API/compare/v10.2.0...v10.2.1) (2023-05-29)


### Bug Fixes

* **database:** Fixed column type check and handling for SQLite ([b90cc72](https://github.com/GeorgeV220/API/commit/b90cc7261c7fc3fb603eb9e821a1958b9ff8b012))

# [10.2.0](https://github.com/GeorgeV220/API/compare/v10.1.4...v10.2.0) (2023-05-29)


### Features

* **database:** Simplify createTable method and add insert and update statement builders ([b15e95d](https://github.com/GeorgeV220/API/commit/b15e95d1a6f91111ff76b2cdfc054dddb0f6f9f1))
* **db:** Simplify createTable method and add insert/update builders ([6321536](https://github.com/GeorgeV220/API/commit/63215360670cf0344e93521c97fcf875f9bda61f)), closes [#102](https://github.com/GeorgeV220/API/issues/102)

## [10.1.4](https://github.com/GeorgeV220/API/compare/v10.1.3...v10.1.4) (2023-05-29)


### Bug Fixes

* entity serialization and deserialization ([574d94c](https://github.com/GeorgeV220/API/commit/574d94ca470b48aaa10edc31fd4a3c93da978803))

## [10.1.3](https://github.com/GeorgeV220/API/compare/v10.1.2...v10.1.3) (2023-05-29)


### Bug Fixes

* Remove unnecessary argument in executeQuery() call ([b148d37](https://github.com/GeorgeV220/API/commit/b148d373d9f73ea0ddc17f802cf0dd89eea299b7))

## [10.1.2](https://github.com/GeorgeV220/API/compare/v10.1.1...v10.1.2) (2023-05-21)


### Bug Fixes

* Improve JavaExtensionClassLoader and JavaExtensionLoader ([2b40e6d](https://github.com/GeorgeV220/API/commit/2b40e6dd83500be2fb35e5ee070342a098b29f9b))

## [10.1.1](https://github.com/GeorgeV220/API/compare/v10.1.0...v10.1.1) (2023-05-03)


### Bug Fixes

* **EntityManager:** Class cast exception ([278056c](https://github.com/GeorgeV220/API/commit/278056cec4f4b0bb0e695a0f4d58280cfafd69d4))

# [10.1.0](https://github.com/GeorgeV220/API/compare/v10.0.0...v10.1.0) (2023-05-03)


### Features

* **EntityManager:** Fix SQL and use the new (de)serializers ([caa98b5](https://github.com/GeorgeV220/API/commit/caa98b51e8cde4f8476e59d60eb304fb27af6136))
* **Utils:** Added new methods to serialize/deserialize objects. ([38368d1](https://github.com/GeorgeV220/API/commit/38368d1064393ed4ae5e6aa6ee7cd9e072e0592a))

# [10.0.0](https://github.com/GeorgeV220/API/compare/v9.9.2...v10.0.0) (2023-05-02)


### Bug Fixes

* **build:** Fixed build warnings and errors ([88aa2c4](https://github.com/GeorgeV220/API/commit/88aa2c479ab85e98b34a6bb8f9e166598ab7dce5))


### Features

* **EntityManager:** Changed UserManager to EntityManager ([638f592](https://github.com/GeorgeV220/API/commit/638f592605b4cb9136e2d985522accf40013f3be))


### BREAKING CHANGES

* **EntityManager:** INCOMPATIBLE WITH THE PREVIOUS VERSION

## [9.9.2](https://github.com/GeorgeV220/API/compare/v9.9.1...v9.9.2) (2023-04-18)


### Bug Fixes

* **build:** Fixed build warnings and errors ([#95](https://github.com/GeorgeV220/API/issues/95)) ([057b0f3](https://github.com/GeorgeV220/API/commit/057b0f34e6c2148a291d11982db45716e09598de))

## [9.9.1](https://github.com/GeorgeV220/API/compare/v9.9.0...v9.9.1) (2023-04-18)


### Bug Fixes

* **build:** Fixed build warnings and errors ([b896713](https://github.com/GeorgeV220/API/commit/b8967137af61575b067bbdef70b46723d3bb2b74))

# [9.9.0](https://github.com/GeorgeV220/API/compare/v9.8.0...v9.9.0) (2023-04-18)


### Features

* **CompletableFutureManager:** Add utility class for managing CompletableFuture instances ([a03ef07](https://github.com/GeorgeV220/API/commit/a03ef07c935042da9496f546724f57b6cf3e5523))
* **ObservableObjectMap:** Added methods to remove and retrieve MapChangeListeners ([99fa7cd](https://github.com/GeorgeV220/API/commit/99fa7cd9ee0afe60bd3296e20ea8ed691eadd26a))
* **UserManager:** Update constructor for storage systems ([83d3eb4](https://github.com/GeorgeV220/API/commit/83d3eb4874c1325717fbf39047564868501efa59))

# [9.8.0](https://github.com/GeorgeV220/API/compare/v9.7.0...v9.8.0) (2023-04-09)


### Bug Fixes

* **UserManager:** Changed loadedUsers to ObservableObjectMap ([7c9f424](https://github.com/GeorgeV220/API/commit/7c9f424356c1b369a649863f8187815c9131dcbb))


### Features

* **ObservableObjectMap:** JavaDocs and new methods ([c633b34](https://github.com/GeorgeV220/API/commit/c633b340ddc2810c748137029f1b78e543364263))
* **UserManager:** Added UserManager.User.toString ([b44870b](https://github.com/GeorgeV220/API/commit/b44870b6106d9a2170cee3807ac5b3510172a3c0))

# [9.7.0](https://github.com/GeorgeV220/API/compare/v9.6.0...v9.7.0) (2023-04-08)


### Features

* **PairDocument:** Changed from ObjectMap.Pair<Object, Object> to ObjectMap.Pair<K, V> ([d1f23bc](https://github.com/GeorgeV220/API/commit/d1f23bce1fa55f6a93cbe0365865a9f672fc2ce3))
* **UserManager:** New methods and deprecation ([e6bbb19](https://github.com/GeorgeV220/API/commit/e6bbb196b97c263d704a46ced06b719695256b2f))

# [9.6.0](https://github.com/GeorgeV220/API/compare/v9.5.1...v9.6.0) (2023-04-08)


### Features

* **ObservableObjectMap:** Added ObservableObjectMap ([049f676](https://github.com/GeorgeV220/API/commit/049f67617f867c275c5c3f7568cfec2f31c80244))
* **UserManager:** Changed ConcurrentObjectMap to ObservableObjectMap ([24fa0a4](https://github.com/GeorgeV220/API/commit/24fa0a4095c84698bf5b0ed35576ce795a4d058c))
* **UserManager:** Gson changes ([6a6a66a](https://github.com/GeorgeV220/API/commit/6a6a66a6b98c9697d6e6a1664b2b0b399d7e2ac9))

## [9.5.1](https://github.com/GeorgeV220/API/compare/v9.5.0...v9.5.1) (2023-03-16)


### Bug Fixes

* PUBLISH ([e74001f](https://github.com/GeorgeV220/API/commit/e74001f212b38ac9ca54286955845a1162aabb35))

# [9.5.0](https://github.com/GeorgeV220/API/compare/v9.4.0...v9.5.0) (2023-03-16)


### Bug Fixes

* **MinecraftVersion:** 1.19.4 support ([0af0ba9](https://github.com/GeorgeV220/API/commit/0af0ba93618226d9ea1a0bf855491d6252668b7a))


### Features

* 1.20 R1 for future release ([cc75b40](https://github.com/GeorgeV220/API/commit/cc75b404fc6cf07d9c4e6d9c9ce802c93b3156e3))

## [9.4.1](https://github.com/GeorgeV220/API/compare/v9.4.0...v9.4.1) (2023-03-16)


### Bug Fixes

* **MinecraftVersion:** 1.19.4 support ([0af0ba9](https://github.com/GeorgeV220/API/commit/0af0ba93618226d9ea1a0bf855491d6252668b7a))

# [9.4.0](https://github.com/GeorgeV220/API/compare/v9.3.0...v9.4.0) (2023-02-13)


### Features

* **Database:** UserManager utility ([e34a643](https://github.com/GeorgeV220/API/commit/e34a643e650df8f0f0ac07ac78866d4a4fbf9c41))
* **ObjectMap:** ObjectMap GSON (De)Serialization ([53376d9](https://github.com/GeorgeV220/API/commit/53376d98e890cff511dbff04b16fcc335955edeb))

# [9.3.0](https://github.com/GeorgeV220/API/compare/v9.2.0...v9.3.0) (2023-02-13)


### Features

* **Database:** Added getMongoDB ([b632ef9](https://github.com/GeorgeV220/API/commit/b632ef91deb74eb735190c91d89d760fdcae9ac4))

# [9.2.0](https://github.com/GeorgeV220/API/compare/v9.1.1...v9.2.0) (2023-02-05)


### Features

* **Yaml:** CFG replace constructor ([05857b8](https://github.com/GeorgeV220/API/commit/05857b83c55ecb1d989d26d0a6504c109906ea7f))

## [9.1.1](https://github.com/GeorgeV220/API/compare/v9.1.0...v9.1.1) (2023-01-22)


### Bug Fixes

* Fixed package and sponge7 implementation ([40c15db](https://github.com/GeorgeV220/API/commit/40c15db1a9184a84307222edc0dd0addec90ecee))

# [9.1.0](https://github.com/GeorgeV220/API/compare/v9.0.0...v9.1.0) (2023-01-22)


### Features

* **Minecraft:** Shade minecraft classes to one jar ([08f03ae](https://github.com/GeorgeV220/API/commit/08f03aeb480983e7eb079a7693cb69126c038f35))

# [9.0.0](https://github.com/GeorgeV220/API/compare/v8.15.0...v9.0.0) (2023-01-21)


### Features

* Sub modules project setup and Sponge7 support ([be752de](https://github.com/GeorgeV220/API/commit/be752de303199e47c7b9d619e42ccc3b1449a022))


### BREAKING CHANGES

* Maven/Gradle artifacts changed

# [8.15.0](https://github.com/GeorgeV220/API/compare/v8.14.0...v8.15.0) (2023-01-20)


### Features

* **LoggerWrapper:** Added SLF4J Logger ([6956918](https://github.com/GeorgeV220/API/commit/6956918ea61b7fb1db97bf4d096ed4f2177d1653))

# [8.14.0](https://github.com/GeorgeV220/API/compare/v8.13.0...v8.14.0) (2023-01-20)


### Features

* **VelocityMinecraftUtils:** Removed ProxyServer from methods varargs ([7165d46](https://github.com/GeorgeV220/API/commit/7165d46c1f5685427409595fae7939555d40cd1b))

# [8.13.0](https://github.com/GeorgeV220/API/compare/v8.12.0...v8.13.0) (2023-01-17)


### Features

* **MinecraftVersion:** Added SpongeMinecraftUtils.MinecraftVersion ([d655a8f](https://github.com/GeorgeV220/API/commit/d655a8f050b1a9aa0b248472530ea118eb5ac6d0))

# [8.12.0](https://github.com/GeorgeV220/API/compare/v8.11.2...v8.12.0) (2023-01-17)


### Features

* **Reflection:** Changed on how method fetches/invokes work ([d4024c8](https://github.com/GeorgeV220/API/commit/d4024c8a49287cb28c33436e73c321d92e3d74b4))

## [8.11.2](https://github.com/GeorgeV220/API/compare/v8.11.1...v8.11.2) (2023-01-15)


### Bug Fixes

* **VelocityMinecraftUtils:** registerListeners ([d3506f1](https://github.com/GeorgeV220/API/commit/d3506f1037d7b744c705d8cfe8aaee4a3f95abaa))

## [8.11.1](https://github.com/GeorgeV220/API/compare/v8.11.0...v8.11.1) (2023-01-15)


### Bug Fixes

* **SpongeMinecraftUtils:** registerListeners ([c15a27a](https://github.com/GeorgeV220/API/commit/c15a27a986ebeb9ca809ecf128b8d2e68f34bcbf))

# [8.11.0](https://github.com/GeorgeV220/API/compare/v8.10.1...v8.11.0) (2023-01-15)


### Features

* **Sponge:** Added Sponge support. ([279d9ef](https://github.com/GeorgeV220/API/commit/279d9efad1dcb4bbadbb70267033286305ff5d24))

## [8.10.1](https://github.com/GeorgeV220/API/compare/v8.10.0...v8.10.1) (2023-01-11)


### Bug Fixes

* **Reflection:** fetchField object null checker ([603d759](https://github.com/GeorgeV220/API/commit/603d759e3f088f2a050aecfb6c737041a2fc6779))

# [8.10.0](https://github.com/GeorgeV220/API/compare/v8.9.0...v8.10.0) (2023-01-11)


### Features

* **Reflection:** Added new methods for declared fields/methods ([0c128c1](https://github.com/GeorgeV220/API/commit/0c128c140e0e28421f5d92dec94fe611c3c8f4ff))

# [8.9.0](https://github.com/GeorgeV220/API/compare/v8.8.0...v8.9.0) (2023-01-11)


### Features

* **Reflection:** Added getFieldByType and getFieldByTypeList ([d4180b5](https://github.com/GeorgeV220/API/commit/d4180b5d59f30fee4a19ec44cb5c79edae9c6482))

# [8.8.0](https://github.com/GeorgeV220/API/compare/v8.7.0...v8.8.0) (2023-01-11)


### Features

* **Reflection:** Added new Reflection methods ([83a9057](https://github.com/GeorgeV220/API/commit/83a9057ea9c66317e663661ca88aaff7368be8a2))

# [8.7.0](https://github.com/GeorgeV220/API/compare/v8.6.0...v8.7.0) (2023-01-11)


### Features

* **Reflection:** Added getEnum and getSubClass methods ([3286c62](https://github.com/GeorgeV220/API/commit/3286c62c1777d5af2c08fd739ffdca9293b03fe1))
* **Reflection:** Added new getNMSClass methods ([1addc61](https://github.com/GeorgeV220/API/commit/1addc618d588a4d258283b01c4b2618cc17f1025))

# [8.6.0](https://github.com/GeorgeV220/API/compare/v8.5.1...v8.6.0) (2023-01-07)


### Bug Fixes

* **Velocity:** Fixed printMsg(ProxyServer,List<String>) ([4129e7c](https://github.com/GeorgeV220/API/commit/4129e7c58d1a9639db6be9cb7bea13cd2909792e))


### Features

* **MinecraftUtils:** New printMsg and broadcastMsg ([4d0dcdc](https://github.com/GeorgeV220/API/commit/4d0dcdcab8eb46969de6a14136756ec9c787cca0))

## [8.5.1](https://github.com/GeorgeV220/API/compare/v8.5.0...v8.5.1) (2023-01-07)


### Bug Fixes

* **Velocity:** Fixed printMsg and broadcastMsg ([c671e9d](https://github.com/GeorgeV220/API/commit/c671e9de64c8680b6e917263dfbc09d04d037260))

# [8.5.0](https://github.com/GeorgeV220/API/compare/v8.4.0...v8.5.0) (2023-01-06)


### Bug Fixes

* **build.gradle:** Velocity Maven repository ([4b6a64b](https://github.com/GeorgeV220/API/commit/4b6a64bf56930bb58577547d9218c73c63c5d523))
* **gradle.yml:** Bump node to 18 ([0f9ceca](https://github.com/GeorgeV220/API/commit/0f9cecaaf66ff4ca96808d323f989982dc29bea1))
* **gradle.yml:** Change to ubuntu latest ([b166f02](https://github.com/GeorgeV220/API/commit/b166f0244533e94f8db22b307d949c6576439b52))


### Features

* **VelocityMinecraftUtils:** Added VelocityMinecraftUtils ([c47436d](https://github.com/GeorgeV220/API/commit/c47436d68a9f21134b52d8ccc5d7927485adc934))

# [8.4.0](https://github.com/GeorgeV220/API/compare/v8.3.1...v8.4.0) (2022-12-23)


### Features

* **MongoDB:** Removed deprecated construction MongoDB(String,int,String,String,String,String) ([9af304b](https://github.com/GeorgeV220/API/commit/9af304b8d5327d8bce467831af3180d649ccc5d5))

## [8.3.1](https://github.com/GeorgeV220/API/compare/v8.3.0...v8.3.1) (2022-12-23)


### Bug Fixes

* **DatabaseWrapper:** MongoDB Database ([1d13d99](https://github.com/GeorgeV220/API/commit/1d13d9962707aa587ffde5dd5bf0530dea5ae6c7))

# [8.3.0](https://github.com/GeorgeV220/API/compare/v8.2.2...v8.3.0) (2022-12-12)


### Bug Fixes

* **BukkitMinecraftUtils:** Added 1.19.2 (1_19_R2) ([133b930](https://github.com/GeorgeV220/API/commit/133b9305132356576ab1e1700ea1b05d7149ee0f))
* **BungeeMinecraftUtils:** Added import for FileConfiguration ([2fd9398](https://github.com/GeorgeV220/API/commit/2fd939811b3d4c3df78bc734977f61c15aa0494b))
* **BungeeMinecraftUtils:** Remove MinecraftUtils imports ([9a24486](https://github.com/GeorgeV220/API/commit/9a24486319a2636b047712258fd3e63f233c6a0f))


### Features

* **MinecraftUtils:** Changed MinecraftUtils to BukkitMinecraftUtils ([8cf6d31](https://github.com/GeorgeV220/API/commit/8cf6d31282db12beae36595eab697dbf8486d667))

## [8.2.2](https://github.com/GeorgeV220/API/compare/v8.2.1...v8.2.2) (2022-12-07)


### Bug Fixes

* **BungeeMinecraftUtils:** Remove MinecraftUtils imports ([#35](https://github.com/GeorgeV220/API/issues/35)) ([3d78570](https://github.com/GeorgeV220/API/commit/3d78570e573be1375a9ffcf94a22155be40e79b9))

## [8.2.1](https://github.com/GeorgeV220/API/compare/v8.2.0...v8.2.1) (2022-12-05)


### Bug Fixes

* **BungeeMinecraftUtils:** Changed target.sendMessage with target.sendMessages ([8db98e6](https://github.com/GeorgeV220/API/commit/8db98e624b62f857d76227b0f65016e708dabd7d))

# [8.2.0](https://github.com/GeorgeV220/API/compare/v8.1.0...v8.2.0) (2022-12-05)


### Features

* **BungeeMinecraftUtils:** Added BungeeMinecraftUtils for BungeeCord ([890c62b](https://github.com/GeorgeV220/API/commit/890c62b59e8802131ee1244edc55c3eb63722a5c))

# [8.1.0](https://github.com/GeorgeV220/API/compare/v8.0.0...v8.1.0) (2022-11-21)


### Features

* **SerializableLocation:** Added SerializableLocation to MinecraftUtils ([d789d1e](https://github.com/GeorgeV220/API/commit/d789d1e00e582c2d0da9f2c36df5946552373e12))

# [8.0.0](https://github.com/GeorgeV220/API/compare/v7.3.0...v8.0.0) (2022-11-19)


### Features

* **MartexLibrary:** Changed API to MartexLibrary ([62e2ca3](https://github.com/GeorgeV220/API/commit/62e2ca3efe45d5ebb51e4961d231d318f995006d))


### BREAKING CHANGES

* **MartexLibrary:** Changed API name to MartexLibrary

# [7.3.0](https://github.com/GeorgeV220/API/compare/v7.2.0...v7.3.0) (2022-11-19)


### Bug Fixes

* **JavaExtension:** Fixed method checks ([7c8d902](https://github.com/GeorgeV220/API/commit/7c8d902ddf4ff1d362bb26c9b1c35d248bf1a865))


### Features

* **ExtensionManager:** Register class instances ([3f2d83d](https://github.com/GeorgeV220/API/commit/3f2d83ddacbdab67a33ac0cba33c863fe8636b9b))

# [7.2.0](https://github.com/GeorgeV220/API/compare/v7.1.1...v7.2.0) (2022-11-10)


### Features

* **JavaExtension:** New methods and deprecate ([dafa6d8](https://github.com/GeorgeV220/API/commit/dafa6d8657e6e2f0b25e8c514a3fc6afe5f2333f))

## [7.1.1](https://github.com/GeorgeV220/API/compare/v7.1.0...v7.1.1) (2022-11-09)


### Bug Fixes

* **ExtensionClassLoader:** Wrong check for the loader ([3c84a61](https://github.com/GeorgeV220/API/commit/3c84a616f28eadd152fbd4f295c4339d27f0d15d))

# [7.1.0](https://github.com/GeorgeV220/API/compare/v7.0.0...v7.1.0) (2022-11-09)


### Features

* **Extensions:** New method (getExtensionManager) ([31e1087](https://github.com/GeorgeV220/API/commit/31e1087c1dac8ee9b9069188adfdf92e7b13ecd4))

# [7.0.0](https://github.com/GeorgeV220/API/compare/v6.1.0...v7.0.0) (2022-11-09)


### Features

* **JavaExtension:** Interface ([b73199b](https://github.com/GeorgeV220/API/commit/b73199b850317d2cf82619bf5416deaa2f1cedd8))
* **Maven:** Removed maven library loader ([60b6f49](https://github.com/GeorgeV220/API/commit/60b6f4917b5c9782bc702d810d699b462f78e91b))


### BREAKING CHANGES

* **Maven:** Removed Maven Library loader.

# [6.1.0](https://github.com/GeorgeV220/API/compare/v6.0.0...v6.1.0) (2022-11-03)


### Features

* **Scheduler:** Bukkit Scheduler for java applications ([284be61](https://github.com/GeorgeV220/API/commit/284be61899c0c55eeeea53c3f38072b06f6bef5d))

# [6.0.0](https://github.com/GeorgeV220/API/compare/v5.13.0...v6.0.0) (2022-11-03)


### Features

* Rewrite Extensions system ([29c600e](https://github.com/GeorgeV220/API/commit/29c600e99ac9e0ffdf311bd6f4942df1d2eb8be3))


### BREAKING CHANGES

* Extensions system rewrite

# [5.13.0](https://github.com/GeorgeV220/API/compare/v5.12.0...v5.13.0) (2022-11-03)


### Features

* **Utils:** Debug methods ([ea97370](https://github.com/GeorgeV220/API/commit/ea9737084678ece5eab46eaf6195ecaeec24beff))

# [5.12.0](https://github.com/GeorgeV220/API/compare/v5.11.0...v5.12.0) (2022-11-03)


### Features

* **LibraryLoader:** Per library version folder ([1d3d944](https://github.com/GeorgeV220/API/commit/1d3d944b4d2037d5beb3fb9ab2192dcca31d64d6))

# [5.11.0](https://github.com/GeorgeV220/API/compare/v5.10.0...v5.11.0) (2022-11-03)


### Features

* **MinecraftUtils:** New methods ([55e2235](https://github.com/GeorgeV220/API/commit/55e2235691db03be9aa133b6f9e69fcf3ef7916c))

# [5.10.0](https://github.com/GeorgeV220/API/compare/v5.9.2...v5.10.0) (2022-10-31)


### Features

* **Extension:** Package requirement ([e856c86](https://github.com/GeorgeV220/API/commit/e856c865ee7c0291cc2fd22bba35be707fbf5818))

## [5.9.2](https://github.com/GeorgeV220/API/compare/v5.9.1...v5.9.2) (2022-10-31)


### Bug Fixes

* **Extension:** Changed some methods from protected to public ([65b7cc6](https://github.com/GeorgeV220/API/commit/65b7cc61b0562d6a2002b0274e6f6ab5c7521412))

## [5.9.1](https://github.com/GeorgeV220/API/compare/v5.9.0...v5.9.1) (2022-10-31)


### Bug Fixes

* **MinecraftUtils:** FileConfiguration API methods ([e6fb11e](https://github.com/GeorgeV220/API/commit/e6fb11e4c8fb4bb75822ce6cd3a164ab900af626))

# [5.9.0](https://github.com/GeorgeV220/API/compare/v5.8.0...v5.9.0) (2022-10-31)


### Features

* **MinecraftUtils:** new debug methods ([639267c](https://github.com/GeorgeV220/API/commit/639267c94f017f00388a8a397922a2f51a550e25))

# [5.8.0](https://github.com/GeorgeV220/API/compare/v5.7.1...v5.8.0) (2022-10-31)


### Features

* **ItemBuilder:** API FileConfiguration support ([51c5327](https://github.com/GeorgeV220/API/commit/51c53271374ee886007dfa6768f692f62666d499))
* **Utils:** API FileConfiguration isList ([da8ba99](https://github.com/GeorgeV220/API/commit/da8ba990f362303e21a631489cf61a773905305b))

## [5.7.1](https://github.com/GeorgeV220/API/compare/v5.7.0...v5.7.1) (2022-10-31)


### Bug Fixes

* **ExtensionRunnable:** fixed cancel method ([e9e2931](https://github.com/GeorgeV220/API/commit/e9e29319f82598b75c4757882482e8f306ffef7c))

# [5.7.0](https://github.com/GeorgeV220/API/compare/v5.6.0...v5.7.0) (2022-10-31)


### Bug Fixes

* **Extensions:** Cancel all tasks on disable/unload ([c2f70e7](https://github.com/GeorgeV220/API/commit/c2f70e7b1dca98ecc0b68a7d1cec33f007c1a874))


### Features

* **Extensions:** Added IllegalExtensionAccessException ([ff442a7](https://github.com/GeorgeV220/API/commit/ff442a7e5d25f5b6658d96df47eca8abf7136203))
* **Extensions:** Extensions scheduler ([1c43650](https://github.com/GeorgeV220/API/commit/1c43650bb0c9fb9768aecc1dc2734d84e3d268d4))

# [5.6.0](https://github.com/GeorgeV220/API/compare/v5.5.0...v5.6.0) (2022-10-31)


### Bug Fixes

* **ExtensionDescriptionFile:** Deprecation ([1402a81](https://github.com/GeorgeV220/API/commit/1402a81726601072852bd6fe9e486994f8a0dca3))


### Features

* **ExtensionLoader:** Unload and disable methods ([b0b81bf](https://github.com/GeorgeV220/API/commit/b0b81bf04381c5550132a9b4b5096a90ba9a7421))

# [5.5.0](https://github.com/GeorgeV220/API/compare/v5.4.5...v5.5.0) (2022-10-30)


### Features

* **Extensions:** Removed abstraction ([1770665](https://github.com/GeorgeV220/API/commit/17706653610bf1a77af92398e41f8834bf78f3de))
* **MinecraftUtils:** Added methods for extensions to debug messages ([ff283ce](https://github.com/GeorgeV220/API/commit/ff283ce8387137074dd07366931ad565382b7428))

## [5.4.5](https://github.com/GeorgeV220/API/compare/v5.4.4...v5.4.5) (2022-10-26)


### Bug Fixes

* **publish:** Publish source and javadocs ([675dbd9](https://github.com/GeorgeV220/API/commit/675dbd95f491cca65362ddf272e6d93e509c99ea))

## [5.4.4](https://github.com/GeorgeV220/API/compare/v5.4.3...v5.4.4) (2022-10-26)


### Bug Fixes

* **publish.gradle:** Publish repository ([031a3a8](https://github.com/GeorgeV220/API/commit/031a3a833dbbbfd751947280db3ed3726932f4a0))

## [5.4.3](https://github.com/GeorgeV220/API/compare/v5.4.2...v5.4.3) (2022-10-26)


### Bug Fixes

* **publish.gradle:** Static methods ([fab7a99](https://github.com/GeorgeV220/API/commit/fab7a998c0a3aa982430fa79a3310d637dbff15a))

## [5.4.2](https://github.com/GeorgeV220/API/compare/v5.4.1...v5.4.2) (2022-10-26)


### Bug Fixes

* **publish.gradle:** Basic Authentication ([c9f5296](https://github.com/GeorgeV220/API/commit/c9f529630f84afc68a435dc6e338f8a79393e5e9))

## [5.4.1](https://github.com/GeorgeV220/API/compare/v5.4.0...v5.4.1) (2022-10-26)


### Bug Fixes

* **build.gradle:** CI checker ([1b415eb](https://github.com/GeorgeV220/API/commit/1b415ebdfe9586939d716c8152879d256123c095))
* **build.gradle:** Version fix ([298e928](https://github.com/GeorgeV220/API/commit/298e92878b8260282e1bf3dd541bcd38e36b36ba))

# [5.4.0](https://github.com/GeorgeV220/API/compare/v5.3.0...v5.4.0) (2022-10-14)


### Features

* **MinecraftUtils:** HEX Colors ([ff59b30](https://github.com/GeorgeV220/API/commit/ff59b30d6726a6855f324fc3e54b701415a167bc))

# [5.3.0](https://github.com/GeorgeV220/API/compare/v5.2.1...v5.3.0) (2022-07-08)


### Bug Fixes

* **MinecraftUtils:** Added v1.19 to MinecraftVersion. ([01905d0](https://github.com/GeorgeV220/API/commit/01905d05fe4b1e7bcc7e08b1ae380923963ca3cc))


### Features

* **MinecraftUtils:** PlaceholdersAPI support. ([ebda2f6](https://github.com/GeorgeV220/API/commit/ebda2f6c35e9d9d2beb2002dfafbcd89d71f3524))

## [5.2.1](https://github.com/GeorgeV220/API/compare/v5.2.0...v5.2.1) (2022-07-02)


### Bug Fixes

* **MySQL:** Fixed MySQL constructor ([fd7a981](https://github.com/GeorgeV220/API/commit/fd7a981d4bc9153616356ea569638129bb5c58ed))
* **MySQL:** Fixed PostgreSQL constructor ([91024d7](https://github.com/GeorgeV220/API/commit/91024d7dfcb801b5701be024e5f14a9f11e3d151))

# [5.2.0](https://github.com/GeorgeV220/API/compare/v5.1.0...v5.2.0) (2022-05-13)


### Features

* **DatabaseWrapper:** New methods and fixes ([22a4117](https://github.com/GeorgeV220/API/commit/22a411786145c82c473bd92d7dbbdd584428bec8))
* **PairDocument:** Added ObjectMap.PairDocument. ([4ece4ee](https://github.com/GeorgeV220/API/commit/4ece4eec08ff79274fac4d22b66c93321eec2aa2))

# [5.1.0](https://github.com/GeorgeV220/API/compare/v5.0.0...v5.1.0) (2022-05-11)


### Bug Fixes

* **DatabaseType:** Fixed a typo in DatabaseType ([207d2db](https://github.com/GeorgeV220/API/commit/207d2dbb4539678d7694428a3c2495198208c0ce))


### Features

* **Database:** Added getSQLDatabase in DatabaseWrapper ([00f3f9c](https://github.com/GeorgeV220/API/commit/00f3f9c54b8649bd4abdc806fc6fffcf6d9ec4f3))

# [5.0.0](https://github.com/GeorgeV220/API/compare/v4.2.0...v5.0.0) (2022-05-11)


### Features

* **DatabaseWrapper:** Added DatabaseWrapper ([f5599a5](https://github.com/GeorgeV220/API/commit/f5599a5fafe368f65fc516e6b06b3f8c16ca3604))


### BREAKING CHANGES

* **DatabaseWrapper:** New packages for exceptions
New constructors for the SQL classes

# [4.2.0](https://github.com/GeorgeV220/API/compare/v4.1.0...v4.2.0) (2022-05-06)


### Features

* **Callback:** Generify Callback methods ([9a64dca](https://github.com/GeorgeV220/API/commit/9a64dcaf90448d3d541b8d13b6a01e8db5ac2982))

# [4.1.0](https://github.com/GeorgeV220/API/compare/v4.0.0...v4.1.0) (2022-05-06)


### Features

* **ClassLoaderAccess:** Added ClassLoaderAccess#remove(URL) ([58adf4a](https://github.com/GeorgeV220/API/commit/58adf4a0c7a68d9f88a0834035c489539afca626))
* **LibraryLoader:** load(Dependency) is now public and added Dependency#fromString(String) method ([3eeee22](https://github.com/GeorgeV220/API/commit/3eeee224f6a36781b71fdfa95a3f4a933381f5fe))

# [4.0.0](https://github.com/GeorgeV220/API/compare/v3.4.1...v4.0.0) (2022-05-04)


### Features

* **CFG:** Added CFG class ([ddb035a](https://github.com/GeorgeV220/API/commit/ddb035a5613fd360cab86a24f1de84eafea32b61))
* **Log4J:** Removed Log4J ([8fb731e](https://github.com/GeorgeV220/API/commit/8fb731e9de2ce7be2583e8174af0e109952a620e))


### BREAKING CHANGES

* **Log4J:** Replaced Log4J with Java logging implementation.

## [3.4.1](https://github.com/GeorgeV220/API/compare/v3.4.0...v3.4.1) (2022-04-30)


### Bug Fixes

* **MinecraftUtils:** Reflection ([e1aefd2](https://github.com/GeorgeV220/API/commit/e1aefd2e0928615cf4d6e859dd9193ad3904924d))

# [3.4.0](https://github.com/GeorgeV220/API/compare/v3.3.0...v3.4.0) (2022-04-30)


### Features

* **Utils:** Random Element ([8b917c0](https://github.com/GeorgeV220/API/commit/8b917c0e9d2379f52b1d6ebff54e8f6f1bf6aa40))

# [3.3.0](https://github.com/GeorgeV220/API/compare/v3.2.0...v3.3.0) (2022-04-25)


### Features

* **Premium:** New Method ([33d3f1d](https://github.com/GeorgeV220/API/commit/33d3f1d1fde4c8b5c000b982d6837f7fb5b295fe))

# [3.2.0](https://github.com/GeorgeV220/API/compare/v3.1.2...v3.2.0) (2022-04-10)


### Bug Fixes

* **Extension:** Exceptions and changes ([a75f466](https://github.com/GeorgeV220/API/commit/a75f4663b5daaf0cf0380cf76b2b0d51e599d035))
* **ExtensionLoader:** Record constructor ([0e0fb92](https://github.com/GeorgeV220/API/commit/0e0fb925787b4542c23d6705ed06cc40c7474882))
* **ExtensionLoader:** Removed stream.close() ([367e660](https://github.com/GeorgeV220/API/commit/367e6600349150152c909e1ef0fbe65333b04e5c))
* **ExtensionLoader:** Stream closed ([3d25d07](https://github.com/GeorgeV220/API/commit/3d25d0782a223c4fa726bdf9a3c6394efc6029a4))
* **Utils:** Fixed Utils.Assertions.notEmpty ([e465113](https://github.com/GeorgeV220/API/commit/e465113a3869a016a6d56b26b27280aa03b70dd6))


### Features

* **extensions:** Added extensions. ([1c418ab](https://github.com/GeorgeV220/API/commit/1c418ab49dccd30135cb7f47eb0078857af2c34d))
* **Extensions:** addURL and getExtensions ([500a37c](https://github.com/GeorgeV220/API/commit/500a37c2cb116814f4fb95d9c3013d33b8b65eba))

# [3.2.0-alpha.8](https://github.com/GeorgeV220/API/compare/v3.2.0-alpha.7...v3.2.0-alpha.8) (2022-04-10)


### Features

* **Extensions:** addURL and getExtensions ([500a37c](https://github.com/GeorgeV220/API/commit/500a37c2cb116814f4fb95d9c3013d33b8b65eba))

# [3.2.0-alpha.7](https://github.com/GeorgeV220/API/compare/v3.2.0-alpha.6...v3.2.0-alpha.7) (2022-04-10)


### Bug Fixes

* **Extension:** Exceptions and changes ([a75f466](https://github.com/GeorgeV220/API/commit/a75f4663b5daaf0cf0380cf76b2b0d51e599d035))

# [3.2.0-alpha.6](https://github.com/GeorgeV220/API/compare/v3.2.0-alpha.5...v3.2.0-alpha.6) (2022-04-10)


### Bug Fixes

* **Utils:** Fixed Utils.Assertions.notEmpty ([e465113](https://github.com/GeorgeV220/API/commit/e465113a3869a016a6d56b26b27280aa03b70dd6))

# [3.2.0-alpha.5](https://github.com/GeorgeV220/API/compare/v3.2.0-alpha.4...v3.2.0-alpha.5) (2022-04-10)


### Bug Fixes

* **ExtensionLoader:** Removed stream.close() ([367e660](https://github.com/GeorgeV220/API/commit/367e6600349150152c909e1ef0fbe65333b04e5c))

# [3.2.0-alpha.4](https://github.com/GeorgeV220/API/compare/v3.2.0-alpha.3...v3.2.0-alpha.4) (2022-04-10)


### Bug Fixes

* **ExtensionLoader:** Stream closed ([3d25d07](https://github.com/GeorgeV220/API/commit/3d25d0782a223c4fa726bdf9a3c6394efc6029a4))

# [3.2.0-alpha.3](https://github.com/GeorgeV220/API/compare/v3.2.0-alpha.2...v3.2.0-alpha.3) (2022-04-10)


### Bug Fixes

* **ExtensionLoader:** Record constructor ([0e0fb92](https://github.com/GeorgeV220/API/commit/0e0fb925787b4542c23d6705ed06cc40c7474882))

# [3.2.0-alpha.2](https://github.com/GeorgeV220/API/compare/v3.2.0-alpha.1...v3.2.0-alpha.2) (2022-04-10)


### Bug Fixes

* **gradle:** fixed build.gradle ([f3d1dc3](https://github.com/GeorgeV220/API/commit/f3d1dc372f992cf9381ea6d1335a36e14e7f6ae1))

# [3.2.0-alpha.1](https://github.com/GeorgeV220/API/compare/v3.1.1...v3.2.0-alpha.1) (2022-04-10)


### Features

* **extensions:** Added extensions. ([1c418ab](https://github.com/GeorgeV220/API/commit/1c418ab49dccd30135cb7f47eb0078857af2c34d))

## [3.1.1](https://github.com/GeorgeV220/API/compare/v3.1.0...v3.1.1) (2022-04-10)


### Bug Fixes

* **gradle:** Fixed gradle workflow and build.gradle ([4ac20e7](https://github.com/GeorgeV220/API/commit/4ac20e7cb045d3f542903c642de6846cd80b169e))

# [3.1.0](https://github.com/GeorgeV220/API/compare/v3.0.0...v3.1.0) (2022-04-10)


### Features

* **Yaml:** Bukkit Yaml Configuration implementation. ([43ec2e0](https://github.com/GeorgeV220/API/commit/43ec2e0e00bdbdd739fecde840313239d1c3105d))

# [3.0.0](https://github.com/GeorgeV220/API/compare/v2.3.6...v3.0.0) (2022-04-07)


* fix(version) ([e90c167](https://github.com/GeorgeV220/API/commit/e90c167fe9c69175d622ae871601f4f980db9718))


### BREAKING CHANGES

* version??

# 1.0.0 (2022-04-07)


* chore(MinecraftUtils) ([615dca0](https://github.com/GeorgeV220/API/commit/615dca02530d0916567f52372eeb2fd9cfd0f885))


### BREAKING CHANGES

* changed to Java 17 (forgot to add to previous commit)

## [4.5.2](https://github.com/Silthus/spigot-plugin-template/compare/v4.5.1...v4.5.2) (2021-11-20)


### Bug Fixes

* **build:** declutter test log and ignore successes ([018d467](https://github.com/Silthus/spigot-plugin-template/commit/018d467dc2a93a4420edfafdc11abecbae3c7b55))
* **ci:** use jacoco report in publish ([1a67cf0](https://github.com/Silthus/spigot-plugin-template/commit/1a67cf0dbafbb35ec68960368b84c80d74ac7f35))

## [4.5.1](https://github.com/Silthus/spigot-plugin-template/compare/v4.5.0...v4.5.1) (2021-11-20)


### Bug Fixes

* **ci:** invalid unit test results path ([76a65f5](https://github.com/Silthus/spigot-plugin-template/commit/76a65f5b845d58768ffbabec7533b1d8aa1756d3))

# [4.5.0](https://github.com/Silthus/spigot-plugin-template/compare/v4.4.1...v4.5.0) (2021-11-20)


### Features

* **ci:** publish unit test report directly in github ([96430db](https://github.com/Silthus/spigot-plugin-template/commit/96430db69c3c617cb8756d9db0177c1330f163c3))

## [4.4.1](https://github.com/Silthus/spigot-plugin-template/compare/v4.4.0...v4.4.1) (2021-11-14)


### Bug Fixes

* lowercase groupid and artifactid on publish ([f02d7dd](https://github.com/Silthus/spigot-plugin-template/commit/f02d7dd57a8e2ee33ba1535f267e3ec5e6c99550))

# [4.4.0](https://github.com/Silthus/spigot-plugin-template/compare/v4.3.1...v4.4.0) (2021-11-11)


### Features

* add acf-command example incl. tests ([1a71da6](https://github.com/Silthus/spigot-plugin-template/commit/1a71da671f7f3bb693480b9837e3f15d0d8afbb4))
* add example for a test base class ([436f690](https://github.com/Silthus/spigot-plugin-template/commit/436f690ca6f922244e3d820a786e9458d2aab60a))
* add script to update maven and gradle versions inside the readme ([234f0fa](https://github.com/Silthus/spigot-plugin-template/commit/234f0fa737e7db92ac0321a262f94a5a9fde82eb))
* add support for building jdk17 packages with jitpack ([b9eec76](https://github.com/Silthus/spigot-plugin-template/commit/b9eec7669cbca6922eb22be6305e389b0d0214bb))
* add vault economy example incl. tests ([331e961](https://github.com/Silthus/spigot-plugin-template/commit/331e961abc5466a2635166057a1183a92a66d03f))
* **renovate:** group patch and minor dependencies to one PR ([0c0119a](https://github.com/Silthus/spigot-plugin-template/commit/0c0119a6d899478304cbc54f2fa11591fa6071fb))
* upgrade to gradle 7.3 and JDK17 ([9e0aadc](https://github.com/Silthus/spigot-plugin-template/commit/9e0aadc90d85d4e336a9249013f87724420ef55b))

## [4.3.1](https://github.com/Silthus/spigot-plugin-template/compare/v4.3.0...v4.3.1) (2021-10-30)


### Bug Fixes

* **ci:** update the release branch to main ([5654a3c](https://github.com/Silthus/spigot-plugin-template/commit/5654a3c247182489c49714761ad2483e28440112))

# [4.3.0](https://github.com/Silthus/spigot-plugin-template/compare/v4.2.2...v4.3.0) (2021-10-17)


### Bug Fixes

* add default config.yml ([bf36d49](https://github.com/Silthus/spigot-plugin-template/commit/bf36d49e5ce95724433fa24ab356ba962a7ac77b)), closes [#128](https://github.com/Silthus/spigot-plugin-template/issues/128)


### Features

* update to gradle 7.3-rc1 (adds jdk17 support) ([b9a4e7b](https://github.com/Silthus/spigot-plugin-template/commit/b9a4e7b7be83ee572f27c350c63b61909f2a5748))

## [4.2.2](https://github.com/Silthus/spigot-plugin-template/compare/v4.2.1...v4.2.2) (2021-10-04)


### Bug Fixes

* downgrade to jdk 16 ([3791bd2](https://github.com/Silthus/spigot-plugin-template/commit/3791bd2b83201ca648e7dc00ca7cb0c52a26331b))
* publish shaded artifacts ([dc60f45](https://github.com/Silthus/spigot-plugin-template/commit/dc60f45cfa89fe215cf05b16c8002f503a7ae403))

## [4.2.1](https://github.com/Silthus/spigot-plugin-template/compare/v4.2.0...v4.2.1) (2021-09-28)


### Bug Fixes

* restore gradle wrapper jar ([c172046](https://github.com/Silthus/spigot-plugin-template/commit/c172046c56897c69537a4d9cfef336e89e349af9))
* upgrade to matching gradle-wrapper for jdk 17 ([ee642b0](https://github.com/Silthus/spigot-plugin-template/commit/ee642b04252d8f767a1d7848934e7a5cc22e12bf))
* upgrade to matching gradle-wrapper for jdk 17 ([7e35ba7](https://github.com/Silthus/spigot-plugin-template/commit/7e35ba7b44355e2a596e0e03964f951203cc2c98))

# [4.2.0](https://github.com/Silthus/spigot-plugin-template/compare/v4.1.0...v4.2.0) (2021-09-21)


### Features

* upgrade to jdk 17 ([24ae775](https://github.com/Silthus/spigot-plugin-template/commit/24ae775bffa02cc4e30f01b295dde2c0a16e54fe))


### Reverts

* upgrade to jdk 17 ([92036bf](https://github.com/Silthus/spigot-plugin-template/commit/92036bf9b73e1fb531730dff146ed854d7d702fa))

# [4.1.0](https://github.com/Silthus/spigot-plugin-template/compare/v4.0.0...v4.1.0) (2021-09-21)


### Bug Fixes

* **release:** allow semantic-release legacy peer deps ([630364c](https://github.com/Silthus/spigot-plugin-template/commit/630364cfba83dc1b2d227e0c9dc4425908554f6b))


### Features

* **release:** add alpha and beta releases ([1931153](https://github.com/Silthus/spigot-plugin-template/commit/1931153ce8d623f20b75aebdf3c5bcdafb31aa19))
* **release:** remove unnecessary node dependency ([ce926bf](https://github.com/Silthus/spigot-plugin-template/commit/ce926bfe3f511397507565d1872719047499a32d))

# [4.0.0](https://github.com/Silthus/spigot-plugin-template/compare/v3.0.2...v4.0.0) (2021-07-23)


### Bug Fixes

* **release:** update to nodejs 16.5 ([eb6e104](https://github.com/Silthus/spigot-plugin-template/commit/eb6e104b2d89ef7d75a6b0aa12c772fea58d3eaa))


### Features

* **build:** update to java16, minecraft 1.17 and gradle 7 ([6cb9365](https://github.com/Silthus/spigot-plugin-template/commit/6cb93654781d53a2fcfca22b2beff1d595b097a2))
* **ci:** update to jdk16 in gradle ci build ([4c6ee7a](https://github.com/Silthus/spigot-plugin-template/commit/4c6ee7a425353396939d8565d4bc8d60d8b40ba4))


### BREAKING CHANGES

* **build:** now uses java16 to be compatible with minecraft 1.17

## [3.0.2](https://github.com/Silthus/spigot-plugin-template/compare/v3.0.1...v3.0.2) (2021-05-31)


### Bug Fixes

* **idea:** Utilize `$PROJECT_DIR$` in the `WORKING_DIRECTORY` option ([3bef4ee](https://github.com/Silthus/spigot-plugin-template/commit/3bef4eeba90f718ee1909aba058d2b9524a45182))

## [3.0.1](https://github.com/Silthus/spigot-plugin-template/compare/v3.0.0...v3.0.1) (2021-01-11)


### Bug Fixes

* **build:** jacoco coverage report not generated ([90721d9](https://github.com/Silthus/spigot-plugin-template/commit/90721d92fe4b07248214063f1ca89e139dab6cc2))

# [3.0.0](https://github.com/Silthus/spigot-plugin-template/compare/v2.0.1...v3.0.0) (2020-11-17)


### Bug Fixes

* **release:** upgrade to jdk11 ([120683e](https://github.com/Silthus/spigot-plugin-template/commit/120683ea45318ff61a40c02f2cc65fdb41045fc4))


### Features

* add gradle lombok plugin ([d2468bf](https://github.com/Silthus/spigot-plugin-template/commit/d2468bf845b4f0b3c498d197ad641bb46fd341d0))


### BREAKING CHANGES

* **release:** the template now supports jdk11 and upwards

## [2.0.1](https://github.com/Silthus/spigot-plugin-template/compare/v2.0.0...v2.0.1) (2020-07-06)


### Bug Fixes

* **debug:** exclude all content inside debug/ ([751d2b7](https://github.com/Silthus/spigot-plugin-template/commit/751d2b7057a57330851968cb9c9bafad82f09d56))

# [2.0.0](https://github.com/Silthus/spigot-plugin-template/compare/v1.7.1...v2.0.0) (2020-07-06)


### Features

* update spigradle to 2.0.0 ([df1f431](https://github.com/Silthus/spigot-plugin-template/commit/df1f431c6cb68bab085a182970c33cd0e96cddca)), closes [#19](https://github.com/Silthus/spigot-plugin-template/issues/19)


### BREAKING CHANGES

* The `@Plugin` annotation on the plugin main class for spigradle was renamed to `@PluginMain`

## [1.7.1](https://github.com/Silthus/spigot-plugin-template/compare/v1.7.0...v1.7.1) (2020-07-03)


### Bug Fixes

* target Minecraft 1.16.1 in gradle.properties ([8beb34a](https://github.com/Silthus/spigot-plugin-template/commit/8beb34a733787bd4c7cdb814a60234be2b3e981e))

# [1.7.0](https://github.com/Silthus/spigot-plugin-template/compare/v1.6.0...v1.7.0) (2020-07-02)


### Features

* use new spigradle debug task for running the server ([d7a3f25](https://github.com/Silthus/spigot-plugin-template/commit/d7a3f25a694ab92e03ebc7bf9058264b5324acf1))

# [1.6.0](https://github.com/Silthus/spigot-plugin-template/compare/v1.5.2...v1.6.0) (2020-06-25)


### Features

* **build:** split build and test into separate jobs ([38f74d4](https://github.com/Silthus/spigot-plugin-template/commit/38f74d48d258521f9414d1f050cacb7201cf6bfd))

## [1.5.2](https://github.com/Silthus/spigot-plugin-template/compare/v1.5.1...v1.5.2) (2020-06-25)


### Bug Fixes

* **build:** only run once on PR from same repo ([4df7f57](https://github.com/Silthus/spigot-plugin-template/commit/4df7f5701a2c47c46bf5394f544ca5fdf4d8bec4))

## [1.5.1](https://github.com/Silthus/spigot-plugin-template/compare/v1.5.0...v1.5.1) (2020-06-10)


### Bug Fixes

* **docs:** remove invalid whitespaces from gradle.properties ([d3fd4b5](https://github.com/Silthus/spigot-plugin-template/commit/d3fd4b53f1d7c1bd4f331f92b1651f89682b3122))

# [1.5.0](https://github.com/Silthus/spigot-plugin-template/compare/v1.4.0...v1.5.0) (2020-06-10)


### Features

* auto update dependencies with dependabot ([e28ccf4](https://github.com/Silthus/spigot-plugin-template/commit/e28ccf4cbb64ee9975b23c974b50ad1f0a4315f9))

# [1.4.0](https://github.com/Silthus/spigot-plugin-template/compare/v1.3.7...v1.4.0) (2020-06-08)


### Features

* **docs:** add spiget shields to spigot resource ([0907394](https://github.com/Silthus/spigot-plugin-template/commit/090739466d919f7b6f3fe24c3a2fade87129732d))

## [1.3.7](https://github.com/Silthus/spigot-plugin-template/compare/v1.3.6...v1.3.7) (2020-05-25)


### Bug Fixes

* **build:** copy plugin to server based on rootProject.rootDir ([1315050](https://github.com/Silthus/spigot-plugin-template/commit/1315050ac27aa8740672a773ad6fc9fa14ee180e))
* **git:** ignore automatic generated plugin.yml ([4cccd5b](https://github.com/Silthus/spigot-plugin-template/commit/4cccd5b1197311dd456bb742c2ec5cb06dcadb8f))
* **plugin:** set correct spigradle plugin name ([a7d5af5](https://github.com/Silthus/spigot-plugin-template/commit/a7d5af590f8b8b06bf5350bff8e25059cdc4f7d6))

## [1.3.6](https://github.com/Silthus/spigot-plugin-template/compare/v1.3.5...v1.3.6) (2020-05-24)


### Bug Fixes

* **release:** override GitHub API url with GH_URL ([43669f7](https://github.com/Silthus/spigot-plugin-template/commit/43669f730eee93f4c62a9bae1d0666a9513a8046))

## [1.3.5](https://github.com/Silthus/spigot-plugin-template/compare/v1.3.4...v1.3.5) (2020-05-23)


### Bug Fixes

* **build:** hash yarn.lock for cache ([307b744](https://github.com/Silthus/spigot-plugin-template/commit/307b744c7a15fe940d1d8e967e44c93a3063cc67))


### Reverts

* publish shadowJar artifact  ([6e869fc](https://github.com/Silthus/spigot-plugin-template/commit/6e869fcc11de056faad831f215c80538c112435b))

## [1.3.4](https://github.com/Silthus/spigot-plugin-template/compare/v1.3.3...v1.3.4) (2020-05-23)


### Bug Fixes

* **test:** copy plugin.yml to test resources ([54893f2](https://github.com/Silthus/spigot-plugin-template/commit/54893f201d37958e2fc1b07dfb403014d09ff1c0))

## [1.3.3](https://github.com/Silthus/spigot-plugin-template/compare/v1.3.2...v1.3.3) (2020-05-23)


### Bug Fixes

* **plugin:** use pluginName property in plugin.yml ([094c57b](https://github.com/Silthus/spigot-plugin-template/commit/094c57b3730178470b60b7038950be8e1ced6c51))
* **publish:** publish shadow jar artifact ([64968ce](https://github.com/Silthus/spigot-plugin-template/commit/64968cefa69d10fea0f3a26b2d3da7abc1e627d5))

## [1.3.2](https://github.com/Silthus/spigot-plugin-template/compare/v1.3.1...v1.3.2) (2020-05-23)


### Bug Fixes

* **build:** cache node correctly ([20bb9e3](https://github.com/Silthus/spigot-plugin-template/commit/20bb9e349a16efb5712c037b1786358447bfdda1))

## [1.3.1](https://github.com/Silthus/spigot-plugin-template/compare/v1.3.0...v1.3.1) (2020-05-22)


### Bug Fixes

* **release:** update semantic-release/GitHub ([aefa8c1](https://github.com/Silthus/spigot-plugin-template/commit/aefa8c1c9e250dc4df9e1707c096bf7e91b8b79d))

# [1.3.0](https://github.com/Silthus/spigot-plugin-template/compare/v1.2.0...v1.3.0) (2020-05-20)


### Bug Fixes

* **publish:** revert publishing to project namespace ([4f2aa72](https://github.com/Silthus/spigot-plugin-template/commit/4f2aa72fd4aeb065fce5db6b8a137d9aa2e2c148))
* **tests:** output jacoco html report for intellij coverage ([7f94567](https://github.com/Silthus/spigot-plugin-template/commit/7f945670a9d63265be695d9adb21dbf0f24ead2b))


### Features

* **publish:** publish github package to user namespace ([ac38c81](https://github.com/Silthus/spigot-plugin-template/commit/ac38c811219942159901e5a177f71c9bb2e1d22e))

# [1.2.0](https://github.com/Silthus/spigot-plugin-template/compare/v1.1.1...v1.2.0) (2020-05-12)


### Bug Fixes

* **build:** include dependencies in the shadow jar ([803099b](https://github.com/Silthus/spigot-plugin-template/commit/803099bdf08c8a9a465d9d1323e3e65e1d498b7a))


### Features

* **build:** replace artifactory with github packages ([f83a08a](https://github.com/Silthus/spigot-plugin-template/commit/f83a08a3507345e234105cb46a1bcf0b9950816a))
* **build:** target JDK 1.8 ([0cc380c](https://github.com/Silthus/spigot-plugin-template/commit/0cc380cc335780ebba57f46ad035badff6cfe299))

## [1.1.1](https://github.com/Silthus/spigot-plugin-template/compare/v1.1.0...v1.1.1) (2020-05-10)


### Bug Fixes

* **build:** cache yarn.lock instead of package-lock ([c067aab](https://github.com/Silthus/spigot-plugin-template/commit/c067aab502cb076253232e20533449336e54fa15))
* **lint:** remove spotless linting ([cb2f8d1](https://github.com/Silthus/spigot-plugin-template/commit/cb2f8d1e70081e414e6fbe76479d7a2387108070))

# [1.1.0](https://github.com/Silthus/spigot-plugin-template/compare/v1.0.1...v1.1.0) (2020-05-09)


### Bug Fixes

* **build:** use ubuntu-18.04 as build host ([38669f1](https://github.com/Silthus/spigot-plugin-template/commit/38669f1523270edfc35e57f3d0278a2cb976a00c))
* **release:** add github credentials ([c6387b5](https://github.com/Silthus/spigot-plugin-template/commit/c6387b57e79260a55423c5a824353200c5ad1bbd))


### Features

* **publish:** publish artifact to github packages ([29afd59](https://github.com/Silthus/spigot-plugin-template/commit/29afd59e039142340e6ae52aa0dfd4d8891c78fc))

# [1.1.0](https://github.com/Silthus/spigot-plugin-template/compare/v1.0.1...v1.1.0) (2020-05-09)


### Bug Fixes

* **build:** use ubuntu-18.04 as build host ([38669f1](https://github.com/Silthus/spigot-plugin-template/commit/38669f1523270edfc35e57f3d0278a2cb976a00c))
* **release:** add github credentials ([c6387b5](https://github.com/Silthus/spigot-plugin-template/commit/c6387b57e79260a55423c5a824353200c5ad1bbd))


### Features

* **publish:** publish artifact to github packages ([29afd59](https://github.com/Silthus/spigot-plugin-template/commit/29afd59e039142340e6ae52aa0dfd4d8891c78fc))

# [1.1.0](https://github.com/Silthus/spigot-plugin-template/compare/v1.0.1...v1.1.0) (2020-05-09)


### Bug Fixes

* **build:** use ubuntu-18.04 as build host ([38669f1](https://github.com/Silthus/spigot-plugin-template/commit/38669f1523270edfc35e57f3d0278a2cb976a00c))
* **release:** add github credentials ([c6387b5](https://github.com/Silthus/spigot-plugin-template/commit/c6387b57e79260a55423c5a824353200c5ad1bbd))


### Features

* **publish:** publish artifact to github packages ([29afd59](https://github.com/Silthus/spigot-plugin-template/commit/29afd59e039142340e6ae52aa0dfd4d8891c78fc))

## [1.1.1](https://github.com/Silthus/spigot-plugin-template/compare/v1.1.0...v1.1.1) (2020-05-08)


### Bug Fixes

* **release:** add github credentials ([c6387b5](https://github.com/Silthus/spigot-plugin-template/commit/c6387b57e79260a55423c5a824353200c5ad1bbd))

# [1.1.0](https://github.com/mcSilthus/spigot-plugin-template/compare/v1.0.1...v1.1.0) (2020-05-08)


### Bug Fixes

* **build:** use ubuntu-18.04 as build host ([38669f1](https://github.com/mcSilthus/spigot-plugin-template/commit/38669f1523270edfc35e57f3d0278a2cb976a00c))


### Features

* **publish:** publish artifact to github packages ([29afd59](https://github.com/mcSilthus/spigot-plugin-template/commit/29afd59e039142340e6ae52aa0dfd4d8891c78fc))

## [1.0.1](https://github.com/mcSilthus/spigot-plugin-template/compare/v1.0.0...v1.0.1) (2020-05-05)


### Bug Fixes

* **release:** run gradle-release before github ([a3ef1fa](https://github.com/mcSilthus/spigot-plugin-template/commit/a3ef1fa22441879e8de6d27a8af0cd5f2ad581f9))

# 1.0.0 (2020-05-05)


### Bug Fixes

* **gradle:** publish build artifacts to maven ([a997f81](https://github.com/mcSilthus/spigot-plugin-template/commit/a997f8196d7e58f2dbeea46a94f0584ef1be6593))
* **release:** use node 12 for semantic-release ([88166a9](https://github.com/mcSilthus/spigot-plugin-template/commit/88166a91ec37f5fa86324137a45e07126a3072e0))
* **test:** directly reference plugin.yml in test ([97f7fc4](https://github.com/mcSilthus/spigot-plugin-template/commit/97f7fc4956138cf83625026c7134d9fb1eaf3794))
* plugin startup ([134f07f](https://github.com/mcSilthus/spigot-plugin-template/commit/134f07f22badfddfe8e924627a75ec68d3814978))


### Features

* **build:** add semantic versioning ([cdedcfd](https://github.com/mcSilthus/spigot-plugin-template/commit/cdedcfd8315309c030668421a4730a23f5282bec))
