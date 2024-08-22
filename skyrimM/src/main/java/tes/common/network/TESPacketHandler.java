package tes.common.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.Entity;

public class TESPacketHandler {
	public static final SimpleNetworkWrapper NETWORK_WRAPPER = NetworkRegistry.INSTANCE.newSimpleChannel("tes_");

	private TESPacketHandler() {
	}

	@SuppressWarnings({"UnusedAssignment", "ValueOfIncrementOrDecrementUsed"})
	public static void preInit() {
		int id = 0;
		NETWORK_WRAPPER.registerMessage(TESPacketEnableAlignmentZones.Handler.class, TESPacketEnableAlignmentZones.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketFellowshipInvitePlayer.Handler.class, TESPacketFellowshipInvitePlayer.class, id++, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(TESPacketFellowshipAcceptInviteResult.Handler.class, TESPacketFellowshipAcceptInviteResult.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketAchievement.Handler.class, TESPacketAchievement.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketAchievementRemove.Handler.class, TESPacketAchievementRemove.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketAlignment.Handler.class, TESPacketAlignment.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketAlignmentBonus.Handler.class, TESPacketAlignmentBonus.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketAlignmentSee.Handler.class, TESPacketAlignmentSee.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketAnvilEngraveOwner.Handler.class, TESPacketAnvilEngraveOwner.class, id++, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(TESPacketAnvilReforge.Handler.class, TESPacketAnvilReforge.class, id++, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(TESPacketAnvilRename.Handler.class, TESPacketAnvilRename.class, id++, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(TESPacketBannerData.Handler.class, TESPacketBannerData.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketBannerRequestInvalidName.Handler.class, TESPacketBannerRequestInvalidName.class, id++, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(TESPacketBannerValidate.Handler.class, TESPacketBannerValidate.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketBiomeVariantsUnwatch.Handler.class, TESPacketBiomeVariantsUnwatch.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketBiomeVariantsWatch.Handler.class, TESPacketBiomeVariantsWatch.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketBrandingIron.Handler.class, TESPacketBrandingIron.class, id++, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(TESPacketBrewingButton.Handler.class, TESPacketBrewingButton.class, id++, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(TESPacketBrokenPledge.Handler.class, TESPacketBrokenPledge.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketBuyUnit.Handler.class, TESPacketBuyUnit.class, id++, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(TESPacketCWPProtectionMessage.Handler.class, TESPacketCWPProtectionMessage.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketCWPSharedHide.Handler.class, TESPacketCWPSharedHide.class, id++, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(TESPacketCWPSharedHideClient.Handler.class, TESPacketCWPSharedHideClient.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketCWPSharedUnlockClient.Handler.class, TESPacketCWPSharedUnlockClient.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketCancelItemHighlight.Handler.class, TESPacketCancelItemHighlight.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketCape.Handler.class, TESPacketCape.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketCargocart.Handler.class, TESPacketCargocart.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketCargocartControl.Handler.class, TESPacketCargocartControl.class, id++, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(TESPacketCargocartUpdate.Handler.class, TESPacketCargocartUpdate.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketCheckMenuPrompt.Handler.class, TESPacketCheckMenuPrompt.class, id++, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(TESPacketClientInfo.Handler.class, TESPacketClientInfo.class, id++, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(TESPacketClientMQEvent.Handler.class, TESPacketClientMQEvent.class, id++, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(TESPacketClientsideGUI.Handler.class, TESPacketClientsideGUI.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketCoinExchange.Handler.class, TESPacketCoinExchange.class, id++, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(TESPacketConquestGrid.Handler.class, TESPacketConquestGrid.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketConquestGridRequest.Handler.class, TESPacketConquestGridRequest.class, id++, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(TESPacketConquestNotification.Handler.class, TESPacketConquestNotification.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketCreateCWP.Handler.class, TESPacketCreateCWP.class, id++, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(TESPacketCreateCWPClient.Handler.class, TESPacketCreateCWPClient.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketDate.Handler.class, TESPacketDate.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketDeleteCWP.Handler.class, TESPacketDeleteCWP.class, id++, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(TESPacketDeleteCWPClient.Handler.class, TESPacketDeleteCWPClient.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketDeleteMiniquest.Handler.class, TESPacketDeleteMiniquest.class, id++, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(TESPacketDragonControl.Handler.class, TESPacketDragonControl.class, id++, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(TESPacketEditBanner.Handler.class, TESPacketEditBanner.class, id++, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(TESPacketEditNPCRespawner.Handler.class, TESPacketEditNPCRespawner.class, id++, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(TESPacketEditSign.Handler.class, TESPacketEditSign.class, id++, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(TESPacketEntityUUID.Handler.class, TESPacketEntityUUID.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketEnvironmentOverlay.Handler.class, TESPacketEnvironmentOverlay.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketFTBounceClient.Handler.class, TESPacketFTBounceClient.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketFTBounceServer.Handler.class, TESPacketFTBounceServer.class, id++, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(TESPacketFTCooldown.Handler.class, TESPacketFTCooldown.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketFTScreen.Handler.class, TESPacketFTScreen.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketFTTimer.Handler.class, TESPacketFTTimer.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketFactionData.Handler.class, TESPacketFactionData.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketFactionRelations.Handler.class, TESPacketFactionRelations.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketFamilyInfo.Handler.class, TESPacketFamilyInfo.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketFastTravel.Handler.class, TESPacketFastTravel.class, id++, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(TESPacketFellowship.Handler.class, TESPacketFellowship.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketFellowshipCreate.Handler.class, TESPacketFellowshipCreate.class, id++, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(TESPacketFellowshipDisband.Handler.class, TESPacketFellowshipDisband.class, id++, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(TESPacketFellowshipDoPlayer.Handler.class, TESPacketFellowshipDoPlayer.class, id++, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(TESPacketFellowshipLeave.Handler.class, TESPacketFellowshipLeave.class, id++, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(TESPacketFellowshipNotification.Handler.class, TESPacketFellowshipNotification.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketFellowshipPartialUpdate.AddMember.Handler.class, TESPacketFellowshipPartialUpdate.AddMember.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketFellowshipPartialUpdate.ChangeIcon.Handler.class, TESPacketFellowshipPartialUpdate.ChangeIcon.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketFellowshipPartialUpdate.RemoveAdmin.Handler.class, TESPacketFellowshipPartialUpdate.RemoveAdmin.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketFellowshipPartialUpdate.RemoveMember.Handler.class, TESPacketFellowshipPartialUpdate.RemoveMember.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketFellowshipPartialUpdate.Rename.Handler.class, TESPacketFellowshipPartialUpdate.Rename.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketFellowshipPartialUpdate.SetAdmin.Handler.class, TESPacketFellowshipPartialUpdate.SetAdmin.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketFellowshipPartialUpdate.SetOwner.Handler.class, TESPacketFellowshipPartialUpdate.SetOwner.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketFellowshipPartialUpdate.ToggleHiredFriendlyFire.Handler.class, TESPacketFellowshipPartialUpdate.ToggleHiredFriendlyFire.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketFellowshipPartialUpdate.TogglePvp.Handler.class, TESPacketFellowshipPartialUpdate.TogglePvp.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketFellowshipPartialUpdate.ToggleShowMap.Handler.class, TESPacketFellowshipPartialUpdate.ToggleShowMap.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketFellowshipPartialUpdate.UpdatePlayerTitle.Handler.class, TESPacketFellowshipPartialUpdate.UpdatePlayerTitle.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketFellowshipRemove.Handler.class, TESPacketFellowshipRemove.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketFellowshipRename.Handler.class, TESPacketFellowshipRename.class, id++, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(TESPacketFellowshipRespondInvite.Handler.class, TESPacketFellowshipRespondInvite.class, id++, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(TESPacketFellowshipSetIcon.Handler.class, TESPacketFellowshipSetIcon.class, id++, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(TESPacketFellowshipToggle.Handler.class, TESPacketFellowshipToggle.class, id++, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(TESPacketHiredGui.Handler.class, TESPacketHiredGui.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketHiredInfo.Handler.class, TESPacketHiredInfo.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketHiredUnitCommand.Handler.class, TESPacketHiredUnitCommand.class, id++, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(TESPacketHiredUnitDismiss.Handler.class, TESPacketHiredUnitDismiss.class, id++, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(TESPacketHiredUnitInteract.Handler.class, TESPacketHiredUnitInteract.class, id++, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(TESPacketHornSelect.Handler.class, TESPacketHornSelect.class, id++, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(TESPacketInvasionWatch.Handler.class, TESPacketInvasionWatch.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketIsOpRequest.Handler.class, TESPacketIsOpRequest.class, id++, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(TESPacketIsOpResponse.Handler.class, TESPacketIsOpResponse.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketItemSquadron.Handler.class, TESPacketItemSquadron.class, id++, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(TESPacketLocationFX.Handler.class, TESPacketLocationFX.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketLogin.Handler.class, TESPacketLogin.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketLoginPlayerData.Handler.class, TESPacketLoginPlayerData.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketMapTp.Handler.class, TESPacketMapTp.class, id++, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(TESPacketMenuPrompt.Handler.class, TESPacketMenuPrompt.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketMercenaryInteract.Handler.class, TESPacketMercenaryInteract.class, id++, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(TESPacketMessage.Handler.class, TESPacketMessage.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketMiniquest.Handler.class, TESPacketMiniquest.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketMiniquestOffer.Handler.class, TESPacketMiniquestOffer.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketMiniquestOfferClose.Handler.class, TESPacketMiniquestOfferClose.class, id++, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(TESPacketMiniquestRemove.Handler.class, TESPacketMiniquestRemove.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketMiniquestTrack.Handler.class, TESPacketMiniquestTrack.class, id++, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(TESPacketMiniquestTrackClient.Handler.class, TESPacketMiniquestTrackClient.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketMoneyGet.Handler.class, TESPacketMoneyGet.class, id++, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(TESPacketMoneyGive.Handler.class, TESPacketMoneyGive.class, id++, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(TESPacketMountControl.Handler.class, TESPacketMountControl.class, id++, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(TESPacketMountControlServerEnforce.Handler.class, TESPacketMountControlServerEnforce.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketNPCCombatStance.Handler.class, TESPacketNPCCombatStance.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketNPCFX.Handler.class, TESPacketNPCFX.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketNPCIsEating.Handler.class, TESPacketNPCIsEating.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketNPCIsOfferingQuest.Handler.class, TESPacketNPCIsOfferingQuest.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketNPCRespawner.Handler.class, TESPacketNPCRespawner.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketNPCSpeech.Handler.class, TESPacketNPCSpeech.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketNPCSquadron.Handler.class, TESPacketNPCSquadron.class, id++, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(TESPacketOpenSignEditor.Handler.class, TESPacketOpenSignEditor.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketOptions.Handler.class, TESPacketOptions.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketPledge.Handler.class, TESPacketPledge.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketPledgeSet.Handler.class, TESPacketPledgeSet.class, id++, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(TESPacketPortalPos.Handler.class, TESPacketPortalPos.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketRenameCWP.Handler.class, TESPacketRenameCWP.class, id++, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(TESPacketRenameCWPClient.Handler.class, TESPacketRenameCWPClient.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketRenamePouch.Handler.class, TESPacketRenamePouch.class, id++, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(TESPacketSelectCape.Handler.class, TESPacketSelectCape.class, id++, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(TESPacketSelectShield.Handler.class, TESPacketSelectShield.class, id++, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(TESPacketSelectTitle.Handler.class, TESPacketSelectTitle.class, id++, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(TESPacketSell.Handler.class, TESPacketSell.class, id++, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(TESPacketSetOption.Handler.class, TESPacketSetOption.class, id++, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(TESPacketShareCWP.Handler.class, TESPacketShareCWP.class, id++, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(TESPacketShareCWPClient.Handler.class, TESPacketShareCWPClient.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketShield.Handler.class, TESPacketShield.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketStopItemUse.Handler.class, TESPacketStopItemUse.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketTitle.Handler.class, TESPacketTitle.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketTraderInfo.Handler.class, TESPacketTraderInfo.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketTraderInteract.Handler.class, TESPacketTraderInteract.class, id++, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(TESPacketUnitTraderInteract.Handler.class, TESPacketUnitTraderInteract.class, id++, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(TESPacketUpdatePlayerLocations.Handler.class, TESPacketUpdatePlayerLocations.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketUpdateViewingFaction.Handler.class, TESPacketUpdateViewingFaction.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketWaypointRegion.Handler.class, TESPacketWaypointRegion.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketWaypointUseCount.Handler.class, TESPacketWaypointUseCount.class, id++, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(TESPacketWeaponFX.Handler.class, TESPacketWeaponFX.class, id++, Side.CLIENT);
	}

	public static NetworkRegistry.TargetPoint nearEntity(Entity entity, double range) {
		return new NetworkRegistry.TargetPoint(entity.dimension, entity.posX, entity.boundingBox.minY, entity.posZ, range);
	}
}