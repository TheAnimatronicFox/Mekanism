package mekanism.client.gui;

import mekanism.api.gas.GasStack;
import mekanism.api.util.UnitDisplayUtils.TemperatureUnit;
import mekanism.client.gui.element.GuiFluidGauge;
import mekanism.client.gui.element.GuiGauge;
import mekanism.client.gui.element.GuiFluidGauge.IFluidInfoHandler;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.inventory.container.ContainerSolarEvaporationController;
import mekanism.common.tile.TileEntitySolarEvaporationController;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiSolarEvaporationController extends GuiMekanism
{
	public TileEntitySolarEvaporationController tileEntity;

	public GuiSolarEvaporationController(InventoryPlayer inventory, TileEntitySolarEvaporationController tentity)
	{
		super(tentity, new ContainerSolarEvaporationController(inventory, tentity));
		tileEntity = tentity;
		
		guiElements.add(new GuiFluidGauge(new IFluidInfoHandler() {
			@Override
			public FluidTank getTank()
			{
				return tileEntity.inputTank;
			}
		}, GuiGauge.Type.STANDARD, this, MekanismUtils.getResource(ResourceType.GUI, "GuiSolarEvaporationController.png"), 6, 13));
		guiElements.add(new GuiFluidGauge(new IFluidInfoHandler() {
			@Override
			public FluidTank getTank()
			{
				return tileEntity.outputTank;
			}
		}, GuiGauge.Type.STANDARD, this, MekanismUtils.getResource(ResourceType.GUI, "GuiSolarEvaporationController.png"), 152, 13));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		int xAxis = (mouseX - (width - xSize) / 2);
		int yAxis = (mouseY - (height - ySize) / 2);

		fontRendererObj.drawString(LangUtils.localize("container.inventory"), 8, (ySize - 96) + 4, 0x404040);
		fontRendererObj.drawString(tileEntity.getInventoryName(), (xSize/2)-(fontRendererObj.getStringWidth(tileEntity.getInventoryName())/2), 4, 0x404040);

		fontRendererObj.drawString(getStruct(), 50, 21, 0x00CD00);
		fontRendererObj.drawString(LangUtils.localize("gui.height") + ": " + tileEntity.height, 50, 30, 0x00CD00);
		fontRendererObj.drawString(LangUtils.localize("gui.mult") + ": " + getTempMult(), 50, 39, 0x00CD00);
		fontRendererObj.drawString(LangUtils.localize("gui.max") + ": " + getMaxTemp(), 50, 48, 0x00CD00);

		if(xAxis >= 7 && xAxis <= 23 && yAxis >= 14 && yAxis <= 72)
		{
			drawCreativeTabHoveringText(tileEntity.inputTank.getFluid() != null ? LangUtils.localizeFluidStack(tileEntity.inputTank.getFluid()) + ": " + tileEntity.inputTank.getFluidAmount() : LangUtils.localize("gui.empty"), xAxis, yAxis);
		}

		if(xAxis >= 153 && xAxis <= 169 && yAxis >= 14 && yAxis <= 72)
		{
			drawCreativeTabHoveringText(tileEntity.outputTank.getFluid() != null ? LangUtils.localizeFluidStack(tileEntity.outputTank.getFluid()) + ": " + tileEntity.outputTank.getFluidAmount() : LangUtils.localize("gui.empty"), xAxis, yAxis);
		}

		if(xAxis >= 49 && xAxis <= 127 && yAxis >= 64 && yAxis <= 72)
		{
			drawCreativeTabHoveringText(getTemp(), xAxis, yAxis);
		}

		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
	}

	private String getStruct()
	{
		if(tileEntity.structured)
		{
			return LangUtils.localize("gui.formed");
		}
		else {
			if(tileEntity.controllerConflict)
			{
				return LangUtils.localize("gui.conflict");
			}
			else {
				return LangUtils.localize("gui.incomplete");
			}
		}
	}

	private String getTemp()
	{
		float temp = tileEntity.getTemperature()*200;

		return MekanismUtils.getTemperatureDisplay(temp, TemperatureUnit.AMBIENT);
	}

	private String getMaxTemp()
	{
		float temp = tileEntity.getMaxTemperature()*200;

		return MekanismUtils.getTemperatureDisplay(temp, TemperatureUnit.AMBIENT);
	}

	private String getTempMult()
	{
		float temp = (float)Math.round((tileEntity.getTempMultiplier())*10)/10F;

		return temp + "x";
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY)
	{
		mc.renderEngine.bindTexture(MekanismUtils.getResource(ResourceType.GUI, "GuiSolarEvaporationController.png"));
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		int guiWidth = (width - xSize) / 2;
		int guiHeight = (height - ySize) / 2;
		drawTexturedModalRect(guiWidth, guiHeight, 0, 0, xSize, ySize);

		int displayInt = tileEntity.getScaledTempLevel(78);
		drawTexturedModalRect(guiWidth + 49, guiHeight + 64, 176, 59, displayInt, 8);
		
		super.drawGuiContainerBackgroundLayer(partialTick, mouseX, mouseY);
	}
}
