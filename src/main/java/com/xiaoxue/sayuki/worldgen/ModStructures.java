/**
 * Sayuki — Structure registration (stellar_meteor)
 * Compat: Goety-2 and IronsSpellbooks — uses sayuki: namespace structures, no name clash
 */
package com.xiaoxue.sayuki.worldgen;

import com.xiaoxue.sayuki.Sayuki;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModStructures {

    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES =
            DeferredRegister.create(Registries.STRUCTURE_TYPE, Sayuki.MOD_ID);

    public static final DeferredRegister<StructurePieceType> STRUCTURE_PIECE_TYPES =
            DeferredRegister.create(Registries.STRUCTURE_PIECE, Sayuki.MOD_ID);

    public static final RegistryObject<StructureType<StellarMeteorStructure>> METEOR_STRUCTURE_TYPE =
            STRUCTURE_TYPES.register("stellar_meteor",
                    () -> () -> StellarMeteorStructure.CODEC);

    public static final RegistryObject<StructurePieceType> METEOR_PIECE_TYPE =
            STRUCTURE_PIECE_TYPES.register("stellar_meteor",
                    StellarMeteorStructurePiece::createType);

    public static void register(IEventBus bus) {
        STRUCTURE_TYPES.register(bus);
        STRUCTURE_PIECE_TYPES.register(bus);
    }
}
