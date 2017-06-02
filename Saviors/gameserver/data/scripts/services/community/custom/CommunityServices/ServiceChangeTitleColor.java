package services.community.custom.CommunityServices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.community.custom.CommunityServices.handler.CommunityServicesHandler;
import studio.lineage2.gameserver.Config;
import studio.lineage2.gameserver.data.htm.HtmCache;
import studio.lineage2.gameserver.model.Player;
import studio.lineage2.gameserver.network.l2.s2c.ShowBoardPacket;
import studio.lineage2.gameserver.utils.ItemFunctions;

/**
 * Created by Averen on 25.03.2017.
 */
public class ServiceChangeTitleColor extends CommunityServicesHandler
{

    private static final Logger _log = LoggerFactory.getLogger(ServiceChangeTitleColor.class);

    @Override
    public void onInit()
    {
        if(Config.COMMUNITYBOARD_SERVICE_CHANGE_TITLE_COLOR_ENABLED)
        {
            super.onInit();
        }
    }

    @Override
    public String[] getServicesCommands()
    {
        return new String[]{ "serviceChangeCharacterTitleColor", "buyChangeCharacterTitleColor" };
    }

    @Override
    public void onServiceCommand(String bypass, Player player)
    {
        if(bypass.startsWith("serviceChangeCharacterTitleColor"))
        {
            String[] mBypass = bypass.split(" ");
            ShowPage(mBypass[1], player, "");
        }
        else if(bypass.startsWith("buyChangeCharacterTitleColor"))
        {
            String[] mBypass = bypass.split(" ");
            String color;

            if(mBypass.length == 3)
            {
                color = mBypass[2];
                int decodeColor = Integer.decode("0x" + color);

                if(color.equals("FFFFFF"))
                {
                    player.setTitleColor(decodeColor);
                    player.broadcastCharInfo();
                    ShowPage(mBypass[1], player, "Вы возобновили стандартный цвет.");
                }
                else
                {
                    if(Config.COMMUNITYBOARD_SERVICE_CHANGE_TITLE_COLORS.contains(color))
                    {
                        if(ItemFunctions.deleteItem(player, Config.COMMUNITYBOARD_SERVICE_CHANGE_TITLE_COLOR_ITEM, Config.COMMUNITYBOARD_SERVICE_CHANGE_TITLE_COLOR_PRICE))
                        {
                            player.setTitleColor(decodeColor);
                            player.broadcastCharInfo();
                            ShowPage(mBypass[1], player, "Вы успешно сменили цвет титула.");
                        }
                        else
                        {
                            ShowPage(mBypass[1], player, "У вас не достаточно средств.");
                        }
                    }
                    else
                    {
                        ShowPage(mBypass[1], player, "Не верный параметр цвета.");
                    }
                }
            }
            else
            {
                ShowPage(mBypass[1], player, "Не верный параметр цвета.");
            }
        }
    }

    private void ShowPage(String page, Player player, String massage)
    {
        String html;
        html = HtmCache.getInstance().getHtml("scripts/services/community/pages/" + page + ".htm", player);
        html = html.replace("%color%", "<font color=\"" + Integer.toHexString(player.getTitleColor()) +"\">" + Integer.toHexString(player.getNameColor()) + "</font").replace("%massage%", massage);
        ShowBoardPacket.separateAndSend(html, player);
    }
}