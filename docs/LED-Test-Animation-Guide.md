LED Test Animation Guide
=========================

This document explains step-by-step how to install and wire up the LED test assets and animation so the model reacts to a Molang query driven by the BlockEntity value. It assumes the project layout is a typical Minecraft mod with assets under `src/main/resources/assets/<modid>/` and the Java classes provided in the repository.

Target files (where to put assets)
----------------------------------
Place these files inside your mod's resource tree (replace `astreadesigntest` if your modid differs):

- `src/main/resources/assets/astreadesigntest/textures/block/led_test.png`
  - The block/item texture used as the material for the model (atlas fallback)

- `src/main/resources/assets/astreadesigntest/geo/block/led_test.geo.json`
  - The Blockbench-exported geometry file describing the model's bones and mesh.

- `src/main/resources/assets/astreadesigntest/animations/block/led_test.animation.json`
  - The animation JSON exported by Blockbench which references bones and Molang queries.

Required code pieces (already present in the repo)
--------------------------------------------------
The following Java classes handle integrating the BlockEntity, rendering and Molang query registration:

- `com.astrea.astreadesigntest.blockentity.LedTestBlockEntity` — stores `chargePercentage` and registers Geckolib controllers.
- `com.astrea.astreadesigntest.blocks.LedTestBlock` — block implementation that creates the BlockEntity and toggles charge when right-clicked.
- `com.astrea.astreadesigntest.client.renderer.LedTestBlockRenderer` — GeoBlockRenderer wiring the model to the BlockEntity.
- `com.astrea.astreadesigntest.AstreaDesigntestClient` — registers the Molang query used in the animation JSON.

Step-by-step: Adding assets
--------------------------
1. Textures
   - Save your texture image as `led_test.png`.
   - Place it at:
     ```text
     src/main/resources/assets/astreadesigntest/textures/block/led_test.png
     ```
   - If you use Minecraft's block atlas, reference the texture path in your model/materials or animation JSON as `astreacore:item/linktool` style (but using your own mod namespace).

2. Geometry (Blockbench export)
   - In Blockbench create your model and bones.
   - Ensure the bone you want to scale is named consistently with your animation keyframes.
   - Export the geometry as `led_test.geo.json` and place it at:
     ```text
     src/main/resources/assets/astreadesigntest/geo/block/led_test.geo.json
     ```
   - Important: Choose the same name (`led_test`) used by the GeoModel and renderer. The `LedTestModel` in `LedTestBlockRenderer` loads the model by calling
     ```java
     new DefaultedBlockGeoModel(ResourceLocation.fromNamespaceAndPath(AstreaDesigntest.MODID, "led_test"));
     ```
     which expects the `led_test.geo.json` under `geo/block/`.

3. Animation (Blockbench export)
   - Create an animation in Blockbench. When you want to read a variable controlled by code, use a Molang expression in the animation file.
   - Example: scale a bone based on the BlockEntity charge percentage:
     ```json
     "scale": {
       "vector": ["query.astreadesigntest.charge_percentage / 100", 1, 1]
     }
     ```
   - Important: Use a unique, namespaced query name. We use `query.astreadesigntest.charge_percentage` in code.
   - Export the animation as `led_test.animation.json` and place it at:
     ```text
     src/main/resources/assets/astreadesigntest/animations/block/led_test.animation.json
     ```
   - Ensure the animation root name matches the animation invoked by the BlockEntity's Geckolib controller — in this repository `animation.model.new` is used.

Step-by-step: Wiring the Molang variable (code)
---------------------------------------------
1. Register the Molang query on the client side
   - In `AstreaDesigntestClient.onClientSetup`, register a Molang query with Geckolib's `MolangQueries`:
     ```java
     MolangQueries.<LedTestBlockEntity>setActorVariable("query.astreadesigntest.charge_percentage", actor -> actor.animatable().getChargePercentage());
     ```
   - This binds the query name used in the animation JSON to a function that returns the `LedTestBlockEntity`'s charge value.
   - Notes:
     - The generic parameter `<LedTestBlockEntity>` tells Geckolib the type of the animatable.
     - The actor wrapper gives access to the animatable instance.

2. Provide the value in the BlockEntity
   - `LedTestBlockEntity` stores `chargePercentage` and ensures it is persisted to NBT in `saveAdditional` and `loadAdditional`.
   - `setChargePercentage(int)` clamps the value to `[0,100]`. When updating the value at runtime (for example on right-click), call `setChanged()` and `level.sendBlockUpdated(pos, state, state, flags)` on the server to persist and sync to clients.

3. Start the animation loop in the BlockEntity
   - In `LedTestBlockEntity.registerControllers`, the animation controller is created and its predicate calls:
     ```java
     event.getController().setAnimation(RawAnimation.begin().thenLoop("animation.model.new"));
     ```
   - The animation name must match the animation JSON's internal animation ID.

Notes on client/server and syncing
---------------------------------
- Only the server should modify the BlockEntity state (e.g., in `useWithoutItem` server-side branch). After modifying the state you must:
  - call `ledEntity.setChanged()` (or `markChanged()` depending on mappings) to mark the chunk dirty
  - call `level.sendBlockUpdated(pos, state, state, 3)` to notify clients and trigger a block re-render
- The Molang query is evaluated on the client when Geckolib resolves the query inside the animation JSON. For that to work, the client must have the up-to-date value; that's why the server must send updated BlockEntity NBT/state to clients.

Verifying the setup
-------------------
1. Run `./gradlew.bat runClient` and start a new or existing world.
2. Place an `LedTestBlock` in the world.
3. Right-click the block to change the charge (server-side change). The block should update and send the new BlockEntity data to clients.
4. Observe the animation:
   - The bone with the scale transform driven by `query.astreadesigntest.charge_percentage / 100` will scale proportionally to the `chargePercentage` value.

Troubleshooting
---------------
- Nothing changes visually:
  - Confirm the animation JSON uses the exact Molang query name registered in `AstreaDesigntestClient`.
  - Ensure the animation ID in the JSON equals the name started in `animationPredicate` (`animation.model.new`).
  - Verify the `led_test.geo.json` bone names match those referenced by the animation keyframes.
  - Ensure the BlockEntity persists and syncs the value by checking NBT logging or adding temporary logs (but avoid enabling per-frame logs in production).

- Logs flood debug output with repeated messages:
  - The renderer and animation predicate are called often. Avoid logging in those hot paths. Log only on state changes (for example in `setChargePercentage`) to trace changes without spamming.

Appendix: Example flow (quick)
------------------------------
1. Add files to resource paths listed earlier.
2. Confirm `AstreaDesigntestClient` contains:
   ```java
   MolangQueries.<LedTestBlockEntity>setActorVariable("query.astreadesigntest.charge_percentage", actor -> actor.animatable().getChargePercentage());
   ```
3. Confirm the animation JSON uses the same query name:
   ```json
   "vector": ["query.astreadesigntest.charge_percentage / 100", 1, 1]
   ```
4. Run client and test by right-clicking the block.

If you want, I can also:
- Add an example `led_test.animation.json` and a minimal `led_test.geo.json` skeleton to the repository for reference.
- Add a rate-limited debug helper to the codebase to log important changes without flooding the logs.

---
Generated: Oct 17, 2025
