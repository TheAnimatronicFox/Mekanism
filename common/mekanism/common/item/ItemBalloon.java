package mekanism.common.item;

import java.util.List;

import mekanism.api.Coord4D;
import mekanism.api.EnumColor;
import mekanism.common.EntityBalloon;
import mekanism.common.util.MekanismUtils;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBalloon extends ItemMekanism
{
	public ItemBalloon(int id)
	{
		super(id);
		setHasSubtypes(true);
	}
	
	public EnumColor getColor(ItemStack stack)
	{
		return EnumColor.values()[stack.getItemDamage()];
	}
	
	@Override
	public void getSubItems(int i, CreativeTabs tabs, List list)
	{
		for(EnumColor color : EnumColor.values())
		{
			ItemStack stack = new ItemStack(this);
			stack.setItemDamage(color.ordinal());
			list.add(stack);
		}
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer)
	{
		if(!world.isRemote)
		{
			Vector3 vec = new Vector3();
			vec.z += 0.3;
			vec.x -= 0.4;
			vec.rotate(entityplayer.renderYawOffset);
			vec.translate(new Vector3(entityplayer));
			
			world.spawnEntityInWorld(new EntityBalloon(world, vec.x-0.5, vec.y-0.25, vec.z-0.5, getColor(itemstack)));
		}
		
		itemstack.stackSize--;
		
		return itemstack;
	}
	
	@Override
	public String getItemDisplayName(ItemStack stack)
	{
		return getColor(stack).getName() + " " + MekanismUtils.localize("tooltip.balloon");
	}
	
	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
	{
		if(player.isSneaking())
		{
			Coord4D obj = new Coord4D(x, y, z);
			
			if(Block.blocksList[obj.getBlockId(world)].isBlockReplaceable(world, x, y, z))
			{
				obj.yCoord--;
			}
			
			if(canReplace(world, obj.xCoord, obj.yCoord+1, obj.zCoord) && canReplace(world, obj.xCoord, obj.yCoord+2, obj.zCoord))
			{
				world.setBlockToAir(obj.xCoord, obj.yCoord+1, obj.zCoord);
				world.setBlockToAir(obj.xCoord, obj.yCoord+2, obj.zCoord);
				
				if(!world.isRemote)
				{
					world.spawnEntityInWorld(new EntityBalloon(world, obj, getColor(stack)));
				}
				
				stack.stackSize--;
			}
			
			return true;
		}
		
		return false;
	}
	
	/*@Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase entity)
    {
        return false;
    }*/
	
	private boolean canReplace(World world, int x, int y, int z)
	{
		return world.isAirBlock(x, y, z) || Block.blocksList[world.getBlockId(x, y, z)].isBlockReplaceable(world, x, y, z);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister register) {}
}
