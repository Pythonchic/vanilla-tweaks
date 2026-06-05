package com.example.missedpotential.mixin.entity;

import com.example.missedpotential.constants.HorseValues;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mixin(HorseEntity.class)
public abstract class HorseEntityMixin {
    private static final Logger LOGGER = LoggerFactory.getLogger("missed-potential");

    // ========== Разведение ==========
    @Inject(method = "createChild", at = @At("RETURN"))
    private void onCreateChild(ServerWorld world, PassiveEntity other, CallbackInfoReturnable<PassiveEntity> cir) {
        PassiveEntity result = cir.getReturnValue();
        if (!(result instanceof HorseEntity child)) return;

        HorseEntity self = (HorseEntity) (Object) this;
        HorseEntity partner = (HorseEntity) other;

        double fatherSpeed = self.getAttributeBaseValue(EntityAttributes.MOVEMENT_SPEED);
        double motherSpeed = partner.getAttributeBaseValue(EntityAttributes.MOVEMENT_SPEED);

        double childSpeed = ((fatherSpeed + motherSpeed) / 2.0) * HorseValues.BREED_MULTIPLIER;

        double maxInternal = HorseValues.BRED_MAX_SPEED / 42.16;
        if (childSpeed >= maxInternal) {
            childSpeed = maxInternal;
        }

        child.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(childSpeed);
    }
}