package artillects.entity.workers;

import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import artillects.VectorWorld;
import artillects.entity.EntityArtillectGround;
import artillects.entity.ai.EntityAIArtillectFollow;
import artillects.entity.ai.EntityAIBlacksmith;
import artillects.entity.ai.EntityAICrafting;
import artillects.entity.ai.EntityAIMining;
import artillects.entity.ai.EntityAIRangedAttack;
import artillects.hive.ArtillectType;
import artillects.hive.HiveComplex;
import artillects.hive.zone.Zone;
import artillects.hive.zone.ZoneMining;
import artillects.hive.zone.ZoneProcessing;

public class EntityWorker extends EntityArtillectGround
{
    public EntityWorker(World par1World)
    {
        super(par1World);
        this.tasks.addTask(1, new EntityAIRangedAttack(this, 1.0D, 5, 10, 30.0F));
        this.tasks.addTask(2, new EntityAIMining(this, 1));
        this.tasks.addTask(2, new EntityAIBlacksmith(this, 1));
        this.tasks.addTask(2, new EntityAICrafting(this, 1));
        this.tasks.addTask(4, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(5, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(5, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        this.dataWatcher.addObject(EntityArtillectGround.DATA_TYPE_ID, (byte) ArtillectType.HARVESTER.ordinal());
    }

    @Override
    public void setType(ArtillectType type)
    {
        super.setType(type);
        genZone();
    }

    @Override
    public void onEntityUpdate()
    {
        super.onEntityUpdate();
        if (this.getHomePosition().posX == 0 && this.getHomePosition().posY == 0 && this.getHomePosition().posZ == 0)
        {
            this.setHomeArea((int) posX, (int) posY, (int) posZ, 100);
        }
        if (!this.worldObj.isRemote && this.getOwner() instanceof HiveComplex && ((HiveComplex) this.getOwner()).playerZone)
        {
            HiveComplex.getPlayerHive().updateEntity();
            HiveComplex.getPlayerHive().addDrone(this);
            if (this.getZone() == null)
            {
                genZone();
            }
        }
        this.cachedInventory = null;
    }

    @Override
    public void setZone(Zone zone)
    {
        if (!this.worldObj.isRemote && this.getOwner() instanceof HiveComplex && ((HiveComplex) this.getOwner()).playerZone)
        {
            if (zone != null)
                HiveComplex.getPlayerHive().addZone(zone);
        }
    }

    public void genZone()
    {
        if (!this.worldObj.isRemote)
        {
            if (this.getZone() == null)
            {
                if (this.getType() == ArtillectType.HARVESTER)
                {
                    this.setZone(new ZoneMining(HiveComplex.getPlayerHive(), new VectorWorld(this.worldObj, this.getHomePosition().posX - 25, this.getHomePosition().posY - 10, this.getHomePosition().posZ - 25), new VectorWorld(this.worldObj, this.getHomePosition().posX + 25, this.getHomePosition().posY + 10, this.getHomePosition().posZ + 25)));
                }
                if (this.getType() == ArtillectType.BLACKSMITH || this.getType() == ArtillectType.CRAFTER)
                {
                    this.setZone(new ZoneProcessing(HiveComplex.getPlayerHive(), new VectorWorld(this.worldObj, this.getHomePosition().posX - 25, this.getHomePosition().posY - 10, this.getHomePosition().posZ - 25), new VectorWorld(this.worldObj, this.getHomePosition().posX + 25, this.getHomePosition().posY + 10, this.getHomePosition().posZ + 25)));
                }
            }
        }
    }
}