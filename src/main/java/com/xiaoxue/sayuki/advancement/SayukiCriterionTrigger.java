/**
 * Sayuki — Generic criterion trigger for custom advancements.
 * Advances when triggered with a matching item ResourceLocation.
 */
package com.xiaoxue.sayuki.advancement;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

@SuppressWarnings("removal")
public class SayukiCriterionTrigger extends SimpleCriterionTrigger<SayukiCriterionTrigger.TriggerInstance> {
    private final ResourceLocation id;

    public SayukiCriterionTrigger(ResourceLocation id) {
        this.id = id;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    protected TriggerInstance createInstance(JsonObject json, ContextAwarePredicate player,
                                              DeserializationContext context) {
        String itemId = json.has("item") ? json.get("item").getAsString() : null;
        return new TriggerInstance(id, player, itemId);
    }

    /** Trigger this advancement for the given player, optionally matching a specific item. */
    public void trigger(ServerPlayer player, @Nullable Item item) {
        this.trigger(player, instance -> instance.matches(item));
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        @Nullable
        private final String itemId;

        public TriggerInstance(ResourceLocation criterion, ContextAwarePredicate player,
                               @Nullable String itemId) {
            super(criterion, player);
            this.itemId = itemId;
        }

        public boolean matches(@Nullable Item item) {
            if (itemId == null) return true;
            if (item == null) return false;
            ResourceLocation key = ForgeRegistries.ITEMS.getKey(item);
            return key != null && key.toString().equals(itemId);
        }

        @Override
        public JsonObject serializeToJson(SerializationContext context) {
            JsonObject json = super.serializeToJson(context);
            if (itemId != null) {
                json.addProperty("item", itemId);
            }
            return json;
        }
    }
}
