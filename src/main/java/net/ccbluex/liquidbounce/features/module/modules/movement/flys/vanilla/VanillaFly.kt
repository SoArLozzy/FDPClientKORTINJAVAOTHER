package net.ccbluex.liquidbounce.features.module.modules.movement.flys.vanilla

import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.flys.FlyMode
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.features.value.BoolValue
import net.ccbluex.liquidbounce.features.value.FloatValue
import net.minecraft.network.play.client.C00PacketKeepAlive
import net.minecraft.network.play.client.C03PacketPlayer

class VanillaFly : FlyMode("Vanilla") {
    private val speedValue = FloatValue("${valuePrefix}Speed", 2f, 0f, 5f)
    private val vspeedValue = FloatValue("${valuePrefix}Vertical", 2f, 0f, 5f)
    private val kickBypassValue = BoolValue("${valuePrefix}KickBypass", false)
    private val keepAliveValue = BoolValue("${valuePrefix}KeepAlive", false)
    private val noClipValue = BoolValue("${valuePrefix}NoClip", false)
    private val spoofValue = BoolValue("${valuePrefix}SpoofGround", false)

    private var packets = 0

    override fun onEnable() {
        packets = 0
    }

    override fun onUpdate(event: UpdateEvent) {
        if (keepAliveValue.get()) {
            mc.netHandler.addToSendQueue(C00PacketKeepAlive())
        }
        if (noClipValue.get()) {
            mc.thePlayer.noClip = true
        }

        mc.thePlayer.capabilities.isFlying = false
        MovementUtils.resetMotion(true)
        if (mc.gameSettings.keyBindJump.isKeyDown) mc.thePlayer.motionY += vspeedValue.get()

        if (mc.gameSettings.keyBindSneak.isKeyDown) mc.thePlayer.motionY -= vspeedValue.get()

        MovementUtils.strafe(speedValue.get())
    }

    override fun onPacket(event: PacketEvent) {
        val packet = event.packet

        if (packet is C03PacketPlayer) {
            if(spoofValue.get()) packet.onGround = true
            if (packets++ >= 40 && kickBypassValue.get()) {
                packets = 0
                MovementUtils.handleVanillaKickBypass()
            }
        }
    }
}
