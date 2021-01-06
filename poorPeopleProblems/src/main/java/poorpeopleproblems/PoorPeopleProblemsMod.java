package poorpeopleproblems;

import basemod.BaseMod;
import basemod.ModLabeledToggleButton;
import basemod.ModPanel;
import basemod.ModTextPanel;
import basemod.interfaces.PostInitializeSubscriber;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import poorpeopleproblems.util.IDCheckDontTouchPls;
import poorpeopleproblems.util.TextureLoader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

@SpireInitializer
public class PoorPeopleProblemsMod implements
    PostInitializeSubscriber {
  public static final Logger logger = LogManager.getLogger(PoorPeopleProblemsMod.class.getName());
  private static String modID;
  public static Properties poorPeopleProblemsDefaultSettings = new Properties();
  public static final String HOW_FAR_BACK_SHUFFLE = "howFarBackShuffle";
  public static int howFarBackShuffle = 10;
  private static final String MODNAME = "Default Mod";
  private static final String AUTHOR = "Gremious";
  private static final String DESCRIPTION = "A base for Slay the Spire to start your own mod from, feat. the Default.";
  public static final String BADGE_IMAGE = "theDefaultResources/images/Badge.png";

  public PoorPeopleProblemsMod() {
    logger.info("Subscribe to BaseMod hooks");
    BaseMod.subscribe(this);
    setModID("poorpeopleproblems");
    logger.info("Done subscribing");
    logger.info("Adding mod settings");
    poorPeopleProblemsDefaultSettings.setProperty(HOW_FAR_BACK_SHUFFLE, "10");
    try {
      SpireConfig config = new SpireConfig("poorpeopleproblems", "poorPeopleProblemsConfig", poorPeopleProblemsDefaultSettings);
      config.load();
      howFarBackShuffle = config.getInt(HOW_FAR_BACK_SHUFFLE);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    logger.info("Done adding mod settings");
  }

  public static void setModID(String ID) {
    Gson coolG = new Gson();
    InputStream in = PoorPeopleProblemsMod.class.getResourceAsStream("/IDCheckStringsDONT-EDIT-AT-ALL.json");
    IDCheckDontTouchPls EXCEPTION_STRINGS = coolG
        .fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), IDCheckDontTouchPls.class);
    logger.info("You are attempting to set your mod ID as: " + ID);
    if (ID.equals(EXCEPTION_STRINGS.DEFAULTID)) {
      throw new RuntimeException(EXCEPTION_STRINGS.EXCEPTION);
    }
    else if (ID.equals(EXCEPTION_STRINGS.DEVID)) {
      modID = EXCEPTION_STRINGS.DEFAULTID;
    }
    else {
      modID = ID;
    }
    logger.info("Success! ID is " + modID);
  }

  @SuppressWarnings("unused")
  public static void initialize() {
    new PoorPeopleProblemsMod();
  }

  @Override
  public void receivePostInitialize() {
    logger.info("Loading badge image and mod options");
    Texture badgeTexture = TextureLoader.getTexture(BADGE_IMAGE);
    ModPanel settingsPanel = new ModPanel();
    ModLabeledToggleButton enableNormalsButton = new ModLabeledToggleButton(
        "This is the text which goes next to the checkbox.",
        350.0f, 700.0f, Settings.CREAM_COLOR, FontHelper.charDescFont,
        true,
        settingsPanel,
        label -> { },
        button -> {
          howFarBackShuffle = 10;
          try {
            SpireConfig config = new SpireConfig("defaultMod", "theDefaultConfig", poorPeopleProblemsDefaultSettings);
            config.setBool(HOW_FAR_BACK_SHUFFLE, true);
            config.save();
          }
          catch (Exception e) {
            e.printStackTrace();
          }
        });
    settingsPanel.addUIElement(enableNormalsButton);
    BaseMod.registerModBadge(badgeTexture, MODNAME, AUTHOR, DESCRIPTION, settingsPanel);
    logger.info("Done loading badge Image and mod options");
  }
}
