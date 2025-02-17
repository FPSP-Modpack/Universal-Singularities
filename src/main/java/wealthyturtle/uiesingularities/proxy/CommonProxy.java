package wealthyturtle.uiesingularities.proxy;

import cpw.mods.fml.common.IFuelHandler;
import cpw.mods.fml.common.Loader;
import fox.spiteful.avaritia.crafting.CompressOreRecipe;
import fox.spiteful.avaritia.crafting.CompressorManager;
import fox.spiteful.avaritia.crafting.Grinder;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.OreDictionary;
import wealthyturtle.uiesingularities.NEI;
import wealthyturtle.uiesingularities.UniversalSingularity;
import wealthyturtle.uiesingularities.UniversalSingularityItem;
import wealthyturtle.uiesingularities.UniversalSingularityWrapper;

import javax.annotation.Nonnull;

import cpw.mods.fml.common.registry.GameRegistry;

import java.io.File;
import java.util.*;

import static fox.spiteful.avaritia.Config.craftingOnly;
import static fox.spiteful.avaritia.Config.silver;
import static java.io.File.separatorChar;

public class CommonProxy
{
	private final boolean hideDisabledSingularities;
	protected final List<UniversalSingularityItem> singularities = new ArrayList<>();

	private final Map<String, Set<String>> allowed = new HashMap<>();
	private final boolean coalSingularityEasterEgg;

	//Base Values were Generated by random.org [Min: 65, Max: 95]
	/*Instructions on Base Values:
	 * 1) Generate Random Numbers.
	 * 2) Fill Up the Entire Section's Base Values.
	 * 3) [Only Applicable if the Section has 1+ Singularities in it] Check for Any Duplicate Base Values.
	 * 4) [Only Applicable if the Section has 1+ Singularities in it] [If there are Any Duplicate Base Values] Re-roll Using the Random Number Generator, and Repeat from Step 3 Onwards.
	 */
	//Structure of Data: Name, Block Name, Base Value, Lighter Colour Value, Darker Colour Value
	public CommonProxy()
	{
		final List<UniversalSingularity> universalSingularities = Arrays.asList(
				// Vanilla Singularities
				new UniversalSingularity("vanilla", Arrays.asList(
						new UniversalSingularityWrapper("coal", "blockCoal", 24660, 0x282828, 0x0C0C0C, false),
						new UniversalSingularityWrapper("emerald", "blockEmerald", 2800, 0x60DB83, 0x2CA746, false),
						new UniversalSingularityWrapper("diamond", "blockDiamond", 3910, 0x4AEDD1, 0x30DBBD, false)
				)),
				// General (Not Mod Specific)
				new UniversalSingularity("general", Arrays.asList(
						new UniversalSingularityWrapper("aluminum", "blockAluminium", 12345, 0xEDEDED, 0xC5C5C5, false),
						new UniversalSingularityWrapper("brass", "blockBrass", 2380, 0xD89634, 0x8B5F1B, false),
						new UniversalSingularityWrapper("bronze", "blockBronze", 6666, 0xCA904E, 0xC97A25, false),
						new UniversalSingularityWrapper("charcoal", "blockCharcoal", 13370, 0x605543, 0x100E0B, false),
						new UniversalSingularityWrapper("electrum", "blockElectrum", 2000, 0xFDEF5A, 0xEFE252, false),
						new UniversalSingularityWrapper("invar", "blockInvar", 1337, 0xD0D7CE, 0xACB4B1, false),
						new UniversalSingularityWrapper("magnesium", "blockMagnesium", 1020, 0x937C6C, 0x776457),
						new UniversalSingularityWrapper("osmium", "blockOsmium", 420, 0x495dec, 0x2b32af, false),
						new UniversalSingularityWrapper("peridot", "blockPeridot", 960, 0x58A52B, 0x4E9226, false),
						new UniversalSingularityWrapper("ruby", "blockRuby", 2550, 0xB44848, 0x993D3D, false),
						new UniversalSingularityWrapper("sapphire", "blockSapphire", 860, 0x5173D1, 0x466ACE, false),
						new UniversalSingularityWrapper("steel", "blockSteel", 10000, 0x9F9F9F, 0x888888, false),
						new UniversalSingularityWrapper("titanium", "blockTitanium", 280, 0xdbb9da, 0x462b47, false),
						new UniversalSingularityWrapper("tungsten", "blockTungsten", 2480, 0x8b8b8b, 0x161616, false),
						new UniversalSingularityWrapper("uranium", "blockUranium", 1740, 0x00CB40, 0x00C43E, false),
						new UniversalSingularityWrapper("zinc", "blockZinc", 595, 0xdbd4d4, 0x636161, false),
						new UniversalSingularityWrapper("tricalciumphosphate", "blockTricalciumPhosphate", 365, 0xfaef01, 0x696501, false),
						new UniversalSingularityWrapper("palladium", "blockPalladium", 1090, 0xF0F0F0, 0xE2E2E2, false),
						new UniversalSingularityWrapper("iridium", "blockIridium", 1210, 0xffffff, 0x6a6a6a, false),
						new UniversalSingularityWrapper("netherstar", "blockNetherStar", 69, 0xffffff, 0x8884bc, false),
						new UniversalSingularityWrapper("platinum", "blockPlatinum", 1980, 0xf6f3e7, 0xbcbc93, false),
						new UniversalSingularityWrapper("naquadria", "blockNaquadria", 66, 0xffffff, 0x000000, false),
						new UniversalSingularityWrapper("plutonium", "blockPlutonium", 310, 0xcb2a2a, 0x6d100d, false),
						new UniversalSingularityWrapper("meteoricIron", "blockMeteoricIron", 4000, 0x272727, 0x000000, false),
						new UniversalSingularityWrapper("desh", "blockDesh", 2980, 0x383838, 0x000000, false),
						new UniversalSingularityWrapper("europium", "blockEuropium", 2000, 0x3dadada, 0x555555, false),
						new UniversalSingularityWrapper("salt", "blockSalt", 2160, 0xEFEFEF, 0xDDE7E8)
				)),
				// Big Reactors Singularities
				new UniversalSingularity("bigReactors", Arrays.asList(
						new UniversalSingularityWrapper("blutonium", "blockBlutonium", 9999, 0x4642D6, 0x1B00E6, false),
						new UniversalSingularityWrapper("cyanite", "blockCyanite", 9999, 0x5CAFDB, 0x0087EF, false),
						new UniversalSingularityWrapper("graphite", "blockGraphite", 9999, 0x5D5D5D, 0x444444, false),
						new UniversalSingularityWrapper("ludicrite", "blockLudicrite", 9999, 0xF001E8, 0xF103B1, false),
						new UniversalSingularityWrapper("yellorium", "blockYellorium", 9999, 0xD9DB5C, 0xEBFF3D, false)
				)),
				// Draconic Evolution Singularities
				new UniversalSingularity("draconicEvolution", Arrays.asList(
						new UniversalSingularityWrapper("draconium", "blockDraconium", 860, 0x8E5CC0, 0x7C4AAE, false),
						new UniversalSingularityWrapper("draconiumAwakened", "blockDraconiumAwakened", 4, 0xFF7200, 0xFF6600, false)
				)),
				// Ender IO Singularities
				new UniversalSingularity("enderIO", Arrays.asList(
						new UniversalSingularityWrapper("conductiveIron", "blockConductiveIron", 5000, 0xCA9D9D, 0xC39499, false),
						new UniversalSingularityWrapper("electricalSteel", "blockElectricalSteel", 1500, 0x949494, 0x8D8D8D, false),
						new UniversalSingularityWrapper("energeticAlloy", "blockEnergeticAlloy", 2000, 0xFFA638, 0xFF9C21, false),
						new UniversalSingularityWrapper("darkSteel", "blockDarkSteel", 1000, 0x3C3C3C, 0x383838, false),
						new UniversalSingularityWrapper("pulsatingIron", "blockPulsatingIron", 4000, 0x82FA9E, 0x69EA88, false),
						new UniversalSingularityWrapper("redstoneAlloy", "blockRedstoneAlloy", 5000, 0xF25757, 0xD63C3C, false),
						new UniversalSingularityWrapper("soularium", "blockSoularium", 2500, 0x5C4527, 0x5A3E25, false),
						new UniversalSingularityWrapper("vibrantAlloy", "blockVibrantAlloy", 1000, 0xA7CA52, 0x98BB40, false)
				)),
				// ExtraPlanets Singularities
				new UniversalSingularity("extraPlanets", Arrays.asList(
						new UniversalSingularityWrapper("blueGem", "blockBlueGem", 95, 0x38D7FF, 0x03C6F6, false),
						new UniversalSingularityWrapper("carbon", "blockCarbon", 66, 0x1D1D1D, 0x0F0F0F, false),
						new UniversalSingularityWrapper("crystal", "blockCrystal", 420, 0xE2E2E2, 0xC1C1C1, false),
						new UniversalSingularityWrapper("redGem", "blockRedGem", 1310, 0xFF3B0C, 0xF93100, false),
						new UniversalSingularityWrapper("whiteGem", "blockWhiteGem", 70, 0xC6C6C6, 0xA5A5A5, false)
				)),
				// ExtraTiC Singularities
				new UniversalSingularity("extraTiC", Arrays.asList(
						new UniversalSingularityWrapper("fairy", "blockFairy", 90, 0xFF83C3, 0xFF65B4, false),
						new UniversalSingularityWrapper("pokefennium", "blockPokefennium", 71, 0x436B73, 0x485361, false),
						new UniversalSingularityWrapper("red_aurum", "blockRed_aurum", 78, 0xFF4809, 0xFF3D09)
				)),
				// Extra Utilities Singularities
				new UniversalSingularity("extraUtilities", Arrays.asList(
						new UniversalSingularityWrapper("unstable", "blockUnstable", 69, 0xC5C5C5, 0xB1B1B1, false)
				)),
				// Mekanism Singularities
				new UniversalSingularity("mekanism", Arrays.asList(
						new UniversalSingularityWrapper("refinedGlowstone", "blockRefinedGlowstone", 67, 0xF4D036, 0xDBA622, false),
						new UniversalSingularityWrapper("refinedObsidian", "blockRefinedObsidian", 82, 0x57456F, 0x53426A, false)
				)),
				// Metallurgy Singularities
				new UniversalSingularity("metallurgy", Arrays.asList(
						//Utility
						new UniversalSingularityWrapper("bitumen", "blockBitumen", 93, 0x242424, 0x313131),
						new UniversalSingularityWrapper("phosphorus", "blockPhosphorus", 87, 0xA27777, 0x8B5F5F),
						new UniversalSingularityWrapper("potash", "blockPotash", 85, 0xEF9103, 0xDA8403),
						new UniversalSingularityWrapper("saltpeter", "blockSaltpeter", 2920, 0xF0F0F0, 0xE1E1E1),
						new UniversalSingularityWrapper("sulfur", "blockSulfur", 10000, 0xFFF200, 0xFCD703),
						//Alloys - Base
						new UniversalSingularityWrapper("angmallen", "blockAngmallen", 65, 0xE1D78A, 0xD6C761, false),
						new UniversalSingularityWrapper("damascusSteel", "blockDamascusSteel", 84, 0x996D4D, 0x583F2C, false),
						new UniversalSingularityWrapper("hepatizon", "blockHepatizon", 82, 0x755E75, 0x614E61, false),
						//Alloys - Ender
						new UniversalSingularityWrapper("desichalkos", "blockDesichalkos", 88, 0x722FA8, 0x502176, false),
						//Alloys - Fantasy
						new UniversalSingularityWrapper("blackSteel", "blockBlackSteel", 70, 0x395679, 0x314966, false),
						new UniversalSingularityWrapper("celenegil", "blockCelenegil", 81, 0x94CC48, 0x649128, false),
						new UniversalSingularityWrapper("haderoth", "blockHaderoth", 72, 0x77341E, 0x592817, false),
						new UniversalSingularityWrapper("quicksilver", "blockQuicksilver", 79, 0x7CD3C7, 0x2B8073, false),
						new UniversalSingularityWrapper("tartarite", "blockTartarite", 83, 0xAE3400, 0x792400, false),
						//Alloys - Nether
						new UniversalSingularityWrapper("amordrine", "blockAmordrine", 68, 0xA98DB1, 0x8F6B9A, false),
						new UniversalSingularityWrapper("inolashite", "blockInolashite", 67, 0x40AA7D, 0x338864, false),
						new UniversalSingularityWrapper("shadowSteel", "blockShadowSteel", 77, 0x887362, 0x766354, false)
						/*Important Note:
						 * The Rest of the Metallurgy Singularities (That are Non-Utility and Non-Alloy) Can be Found if AOBD Singularities is Used.
						 * For the Other Metallurgy Singularities to be Added, Use AOBD Singularities, with Metallurgy.
						 * (Yes, this is a Shoutout and Semi-Collaboration with RCXcrafter, the Creator of AOBD Singularities, lol.)
						 */
				)),
				// PneumaticCraft Singularities
				new UniversalSingularity("pneumaticCraft", Collections.singletonList(
						new UniversalSingularityWrapper("compressedIron", "blockIronCompressed", 93, 0x636363, 0x515151, false)
				)),
				// ProjectRed Singularities
				new UniversalSingularity("projectRed", Collections.singletonList(
						new UniversalSingularityWrapper("electrotine", "blockElectrotine", 360, 0x0F4985, 0x0D3F72, false)
				)),
				// Redstone Arsenal Singularities
				new UniversalSingularity("redstoneArsenal", Arrays.asList(
						new UniversalSingularityWrapper("electrumFlux", "blockElectrumFlux", 999, 0xD0B64D, 0xA40606, false),
						new UniversalSingularityWrapper("crystalFlux", "blockCrystalFlux", 666, 0xFE333A, 0xE8111A, false)
				)),
				// Tinkers' Construct Singularities
				new UniversalSingularity("tinkersConstruct", Arrays.asList(
						new UniversalSingularityWrapper("aluminumBrass", "blockAluminumBrass", 1824, 0xE2BE4E, 0xD4B148, false),
						new UniversalSingularityWrapper("alumite", "blockAlumite", 229, 0xE9ADDA, 0xE298D1, false),
						new UniversalSingularityWrapper("ardite", "blockArdite", 304, 0xD24900, 0x960000, false),
						new UniversalSingularityWrapper("cobalt", "blockCobalt", 1270, 0x2376DD, 0x023C9B, false),
						new UniversalSingularityWrapper("ender", "blockEnder", 11111, 0x00B293, 0x00927C, false),
						new UniversalSingularityWrapper("glue", "blockGlue", 912, 0xDBD0D0, 0xCEBFBF, false),
						new UniversalSingularityWrapper("manyullyn", "blockManyullyn", 380, 0xA97DE0, 0x926AC3, false)
				))
		);
		final Configuration config = new Configuration(new File("." + separatorChar + "config" + separatorChar + "UniversalSingularities.cfg"));
		hideDisabledSingularities = config.get(Configuration.CATEGORY_GENERAL, "hideDisabledSingularitiesInNei", true).getBoolean() && Loader.isModLoaded("NotEnoughItems");
		universalSingularities.forEach(universalSingularity -> {
			if (config.get(universalSingularity.name, "enabled", true).getBoolean()) {
				for (int i = 0; i < universalSingularity.singularities.size(); i++) {
					final UniversalSingularityWrapper universalSingularityWrapper = universalSingularity.singularities.get(i);
					if (config.get(universalSingularity.name, universalSingularityWrapper.name, universalSingularityWrapper.enabled).getBoolean()) {
						if (!allowed.containsKey(universalSingularity.name))
							allowed.put(universalSingularity.name, new HashSet<>());
						allowed.get(universalSingularity.name).add(universalSingularityWrapper.name);
					}
				}
				singularities.add(new UniversalSingularityItem(universalSingularity.name, universalSingularity.singularities));
			}
		});
		coalSingularityEasterEgg = config.get(Configuration.CATEGORY_GENERAL, "coalSingularityEasterEgg", true).getBoolean();
		if (config.hasChanged())
			config.save();
	}

	public final void preInit()
	{
		singularities.forEach(singularity -> GameRegistry.registerItem(singularity, "universal." + singularity.name + ".singularity"));
	}

	public void init() {}

	public final void postInit()
	{
		if (craftingOnly)
			return;
		addToRecipeInput();
	}

	private void addToRecipeInput()
	{
		singularities.forEach(singularity -> {
			for (int i = 0; i < singularity.universalSingularities.size(); i++) {
				final UniversalSingularityWrapper universalSingularityWrapper = singularity.universalSingularities.get(i);
				if (allowed.containsKey(singularity.name) && allowed.get(singularity.name).contains(universalSingularityWrapper.name)) {
					final List<ItemStack> oreList = OreDictionary.getOres(universalSingularityWrapper.oreName, false);
					if (oreList != null && !oreList.isEmpty()) {
						CompressorManager.getRecipes().add(new CompressOreRecipe(new ItemStack(singularity, 1, i), universalSingularityWrapper.recipeBaseValue, universalSingularityWrapper.oreName, true));
						Grinder.catalyst.getInput().add(new ItemStack(singularity, 1, i));
					} else if (hideDisabledSingularities)
						NEI.hide(new ItemStack(singularity, 1, i));
				} else if (hideDisabledSingularities)
					NEI.hide(new ItemStack(singularity, 1, i));
			}
		});
		if (coalSingularityEasterEgg && allowed.containsKey("vanilla") && allowed.get("vanilla").contains("coal"))
			GameRegistry.registerFuelHandler(new CoalSingularityFuelHandler(getSingularityByName("vanilla")));
	}

	public UniversalSingularityItem getSingularityByName(@Nonnull final String name)
	{
		for (final UniversalSingularityItem singularity : singularities)
			if (singularity.name.equals(name))
				return singularity;
		return null;
	}

	private static class CoalSingularityFuelHandler implements IFuelHandler {
		private final UniversalSingularityItem vanillaSingularity;
		private final int burnTime;

		private CoalSingularityFuelHandler(@Nonnull final UniversalSingularityItem vanillaSingularity) {
			this.vanillaSingularity = vanillaSingularity;
			this.burnTime = 16000 * CompressorManager.getCost(new ItemStack(Blocks.coal_block));
		}

		@Override
		public int getBurnTime(final ItemStack fuel) {
			return fuel != null && fuel.getItem() == vanillaSingularity && fuel.getItemDamage() == 0 ? burnTime : 0;
		}
	}
}
