package com.bloomidea.inspirers.utils;

import com.bloomidea.inspirers.application.AppController;
import com.bloomidea.inspirers.model.Badge;
import com.bloomidea.inspirers.model.Godchild;
import com.bloomidea.inspirers.model.UserBadge;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Created by michellobato on 31/03/17.
 */

public class BadgesAux {
    private static boolean listContainsBadge(ArrayList<UserBadge> userBadges, Badge badge){
        boolean contains = false;

        if(userBadges!=null && badge!=null) {
            for (UserBadge userBadge : userBadges) {
                if (userBadge.getBadge().getCode().equals(badge.getCode())) {
                    contains = true;
                    break;
                }
            }
        }else{
            //nao ganha porque nao da para verificar
            contains = true;
        }

        return contains;
    }

    public static Badge verifyWinBagdeOne(int userTotalMedsInserted, ArrayList<UserBadge> userBadges){
        Badge badgeToWin = AppController.getmInstance().getListBadges().get("1");

        if(listContainsBadge(userBadges,badgeToWin) || userTotalMedsInserted!=0){
            badgeToWin = null;
        }

        return badgeToWin;
    }

    public static boolean verifyWinBagdeTwo(ArrayList<UserBadge> userBadges){
        //todo falta implementar
        return false;
    }

    public static boolean verifyWinBadgeThree(BigDecimal statsWeek, ArrayList<UserBadge> userBadges){
        //todo falta implementar
        return false;
    }

    public static Badge verifyWinBadgeFour(int totalWarriors, ArrayList<UserBadge> userBadges){
        Badge badgeToWin = AppController.getmInstance().getListBadges().get("4");

        if(listContainsBadge(userBadges,badgeToWin) || totalWarriors < 1){
            badgeToWin = null;
        }

        return badgeToWin;
    }

    public static Badge verifyWinBagdeFive(int totalWarriors, ArrayList<UserBadge> userBadges){
        Badge badgeToWin = AppController.getmInstance().getListBadges().get("5");

        if(listContainsBadge(userBadges,badgeToWin) || totalWarriors < 5){
            badgeToWin = null;
        }

        return badgeToWin;
    }

    public static boolean verifyWinBagdeSix(ArrayList<UserBadge> userBadges){
        //todo falta implementar
        return false;
    }

    public static Badge verifyWinBagdeSeven(ArrayList<Godchild> listGodchilds, ArrayList<UserBadge> userBadges){
        Badge badgeToWin = AppController.getmInstance().getListBadges().get("7");

        boolean hasGodchildWithGodchilds = false;

        for(Godchild g : listGodchilds){
            if(g.getProfile().getNumGodChilds()>0){
                hasGodchildWithGodchilds = true;
                break;
            }
        }

        if(listContainsBadge(userBadges,badgeToWin) || !hasGodchildWithGodchilds){
            badgeToWin = null;
        }

        return badgeToWin;
    }

    public static Badge verifyWinBagdeEight(int userPoints, ArrayList<UserBadge> userBadges){
        Badge badge = null;
        Badge badgeToWin = AppController.getmInstance().getListBadges().get("8");

        if(!listContainsBadge(userBadges,badgeToWin)){
            if(userPoints >= 1000){
                badge = badgeToWin;
            }
        }

        return badge;
    }

    public static Badge verifyWinBagdeNine(int userPoints, ArrayList<UserBadge> userBadges){
        Badge badge = null;
        Badge badgeToWin = AppController.getmInstance().getListBadges().get("9");

        if(!listContainsBadge(userBadges,badgeToWin)){
            if(userPoints >= 10000){
                badge = badgeToWin;
            }
        }

        return badge;
    }

    public static Badge verifyWinBagdeTen(int userPoints, ArrayList<UserBadge> userBadges){
        Badge badge = null;
        Badge badgeToWin = AppController.getmInstance().getListBadges().get("10");

        if(!listContainsBadge(userBadges,badgeToWin)){
            if(userPoints >= 100000){
                badge = badgeToWin;
            }
        }

        return badge;
    }

    public static Badge verifyWinBagdeEleven(int userPoints, ArrayList<UserBadge> userBadges){
        Badge badge = null;
        Badge badgeToWin = AppController.getmInstance().getListBadges().get("11");

        if(!listContainsBadge(userBadges,badgeToWin)){
            if(userPoints >= 1000000){
                badge = badgeToWin;
            }
        }

        return badge;
    }

    public static boolean verifyWinBagdeTwelve(ArrayList<UserBadge> userBadges){
        //todo falta implementar
        return false;
    }

    public static boolean verifyWinBagdeThirteen(ArrayList<UserBadge> userBadges){
        //todo falta implementar
        return false;
    }
}
