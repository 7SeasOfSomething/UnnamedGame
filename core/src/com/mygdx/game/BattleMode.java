package com.mygdx.game;

//import javax.smartcardio.Card;
import java.util.*;

/**
 * The BattleMode class, handles the entirety of the battle scenario.
 */
public class BattleMode {
    //Calling instances of the ship class for the enemy and the player.
    //Made public for now, may not be correct.
    public Ship enemyShip;
    public Ship playerShip;
    //The mana count and max for both the player and the enemy.
    private Integer playerMana;
    private Integer enemyMana;
    private Integer playerManaMax;
    private Integer enemyManaMax;
    //ShipEffects, buffs, debuffs, and ailments
    private String playerShipEffects;
    private String enemyShipEffects;
    //The currently held cards for the enemy and the player.
    private List<Card> playerHand = new ArrayList<Card>();
    private List<Card> enemyHand = new ArrayList<Card>();
    ////private Card playerCurrentCard;
    ////private Card enemyCurrentCard;
    //Keeps track of the progression of time, giving both players mana and playing their moves.
    private double clock;
    //The deck that both the enemy and the player brings into the current battle.
    private List<Card> playerTempDeck;
    private List<Card> enemyTempDeck;

    //Temp for ships, not present in the documents but will be here most likely
    private Integer playerShipHealthMax;
    private Integer enemyShipHealthMax;
    private Integer playerShipHealth;
    private Integer enemyShipHealth;
    private Integer playerShipManaRate;
    private Integer enemyShipManaRate;
    private boolean playerShipDead;
    private boolean enemyShipDead;

    private double playerShipDefence; //Goes from 0 to 10 (100%).
    private double enemyShipDefence;






    public BattleMode() {
        playerManaMax = 25;
        enemyManaMax = 5;
        playerMana = 0;
        enemyMana = 0;
        clock = 0;
        //need to define cards and both hands

        Ship playerShip = new Ship();
        Ship enemyShip = new Ship();
        playerShipDead = false;
        enemyShipDead = false;
        playerShip.setManaRegenRate(30); //60 for 1 second, 120 for 2 seconds, etc.
        enemyShip.setManaRegenRate(60);
        playerShipDefence = 0;
        enemyShipDefence = 0;

        //Adding an enemy attack.
        Card basicEnemyAttack = new Card();
        basicEnemyAttack.setEffect("A5");
        basicEnemyAttack.setManaCost(5);
        enemyHand.add(basicEnemyAttack);

        Card basicPlayerAttack = new Card();
        Card basicPlayerDefend = new Card();
        basicPlayerAttack.setEffect("A4");
        basicPlayerAttack.setManaCost(5);
        basicPlayerDefend.setEffect("D5");
        basicPlayerDefend.setManaCost(5);
        playerHand.add(basicPlayerAttack);
        playerHand.add(basicPlayerDefend);

        //These aren't included anywhere, so they probably should be added somewhere, temp here maybe.
        playerShipHealthMax = 20;
        enemyShipHealthMax = 10;
        playerShipHealth = 20;
        enemyShipHealth = 10;
        playerShipManaRate = playerShip.getManaRegenRate();
        enemyShipManaRate = enemyShip.getManaRegenRate();
    }




    /**
     * Takes in the user's card choice and begins to put it into play.
     * @param choice - the user's card choice as an Integer.
     * @return The card that the user plays.
     */
    public Card playerPlayCard(Integer choice){
        if(!(playerHand.isEmpty())){
            return playerHand.get(choice);
        } else {
            return null;
        }
    }

    /**
     * Takes in the enemy's card choice and begins to put it into play.
     * @return The card that the enemy plays.
     */
    public Card enemyPlayCard(){ //Is it really worth the effort to give the enemy a full deck/temp hand? Having 2-4 basic abilities that vary should be enough.
        if(!(enemyHand.isEmpty())){
            return enemyHand.get(0);
        } else {
            return null;
        }
    }

    /**
     * Draws a card from the TempDeck and gives it into the selected person's hand.
     * @param isPlayer - Boolean to see if the draw is for the user or the enemy.
     */
    public void drawCard(Boolean isPlayer){}

    /**
     * Puts the card what was chosen into play.
     * @param card - a given card and its information.
     * @param target - whom the effects of the card apply to.
     */
    public void applyCard(Card card, String target){

        if(card.getEffect().contains("A")){ //attack
            if(target == "player"){
                if(enemyMana >= card.getManaCost()){
                    updateMana("enemy", -card.getManaCost());
                    updateHealth(target, -Character.getNumericValue(card.getEffect().charAt(1)), true);
                }
            } else if (target == "enemy"){
                if(playerMana >= card.getManaCost()){
                    updateMana("player", -card.getManaCost());
                    updateHealth(target, -Character.getNumericValue(card.getEffect().charAt(1)), true);
                }
            }

        }
        if(card.getEffect().contains("D")) { //attack
            if(target == "player"){
                if(playerMana >= card.getManaCost()){
                    updateMana("player", -card.getManaCost());
                    updateDefence("player", Character.getNumericValue(card.getEffect().charAt(1)));

                }


            } else if (target == "enemy"){
                if(enemyMana >= card.getManaCost()){
                    updateMana("enemy", -card.getManaCost());
                    updateDefence("enemy", Character.getNumericValue(card.getEffect().charAt(1)));
                }
            }
        }
    }

    /**
     * Increases the value of someone's mana by 1. (May need to make it so larger than 1 mana increases are possible.
     * @param target - whose mana is going to be updated.
     * @param amount - the amount of mana, positive or negative, that will be applied to the current count.
     */
    public void updateMana(String target, Integer amount){
        if(target == "player"){
            playerMana+=amount;
            if (playerMana > playerManaMax){playerMana = playerManaMax;}
        } else if (target == "enemy"){
            enemyMana+=amount;
            if (enemyMana > enemyManaMax){enemyMana = enemyManaMax;}
        }
    }

    /**
     * Updates the deck of a chosen target, not too sure what this entails, possibly making sure that a drawn card disappears from their deck and cannot be drawn again. (?)
     * @param target - who is going to going to have their deck updated.
     */
    public void updateDeck(String target){}

    /**
     * Updates the health of a target by a given amount, positive or negative.
     * @param target - The ship that will be receiving the health change.
     * @param amount - The amount of said change, positive or negative.
     * @param isAttack - If the reason why the health is being changed is due to an attack.
     *                   This tells us if we need to ignore the defence or not.
     */
    public void updateHealth(String target, Integer amount, boolean isAttack){

        if(target == "player"){
            if(isAttack){
                //To make defence/armour a bit weaker, round up on the damage.
                playerShipHealth+= (int) (amount - Math.ceil(amount*playerShipDefence));
            } else{
                playerShipHealth+=amount;
            }
            if (playerShipHealth > playerShipHealthMax){
                playerShipHealth = playerShipHealthMax;}
            else if (playerShipHealth <= 0){
                //PlayerDied
                playerShipDead = true;
            }

        } else if (target == "enemy"){
            if(isAttack){
                enemyShipHealth+= (int) (amount - Math.ceil(amount*enemyShipDefence));
            } else{
                enemyShipHealth+=amount;
            }
            if (enemyShipHealth > enemyShipHealthMax){
                enemyShipHealth = enemyShipHealthMax;}
            else if (enemyShipHealth <= 0){
                //Enemy Died
                enemyShipDead = true;
            }
        }
    }

    /**
     * Update clock and increment mana if specific ship's regen amount has been reached.
     * This most likely isn't needed and will be in the actual section that does the graphics
     * and actually runs it in the render class. However, this might as well be here as a temporary thing.
     */
    public void updateClock(){
        clock += 1;

        if (clock % playerShipManaRate == 0){
            updateMana("player", 1);
        }
        if (clock % enemyShipManaRate == 0){
            updateMana("enemy", 1);
        }
    }

    /**
     * Show the health current and max for a given target
     * @param target
     * @return a string consisting of (currentHp / maxHp )
     */
    public String showHealthBar(String target) {
        if (target == "player") {
            return "(" + playerShipHealth + "/" + playerShipHealthMax + ")";
        } else if (target == "enemy") {
            return "(" + enemyShipHealth + "/" + enemyShipHealthMax + ")";
        } else{ return null; }
    }

    /**
     * Shows the mana current and max for a given target
     * @param target
     * @return a string consisting of (currentMana / maxMana )
     */
    public String showManaBar(String target) {
        if (target == "player") {
            return "(" + playerMana + "/" + playerManaMax + ")";
        } else if (target == "enemy") {
            return "(" + enemyMana + "/" + enemyManaMax + ")";
        } else{ return null; }
    }

    /**
     * Trying to make an enemy attack the player once its mana is full.
     * God have mercy on my soul.
     */
    public void basicEnemyAI(){
        if(enemyMana == enemyManaMax){
            applyCard(enemyPlayCard(), "player");
        }
    }


    public Boolean gameIsOver(){
        if(playerShipDead || enemyShipDead){
            return true;
        } else {
            return false;
        }
    }

    public void updateDefence(String target, Integer amount){

        if(target == "player"){
            playerShipDefence += (double)amount / 10;
            if(playerShipDefence > 1){
                playerShipDefence = 1; //Still indestructible. Maybe patch that out?
            }
        } else if(target == "enemy"){
            enemyShipDefence += (double)amount / 10;
            if(enemyShipDefence > 1){
                enemyShipDefence = 1;
            }
        }
    }


}
