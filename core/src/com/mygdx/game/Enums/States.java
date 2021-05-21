package com.mygdx.game.Enums;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.TimeUtils;
import com.mygdx.game.Entities.Enemy;
import com.mygdx.game.Entities.Player;

public enum States{
    //Enemy moves and shoots randomly
    MoveRandom{

        @Override
        public States changeState(Enemy enemy, Player player){

            if (enemy.playerInView(player)){
                enemy.setPreviousStateChange(TimeUtils.millis());
                enemy.setTargetArea(enemy.getFlyArea());
                return Attack;
            }
            else if (enemy.bulletInView()){
                enemy.setPreviousStateChange(TimeUtils.millis());
                enemy.setTargetArea(enemy.getFlyArea());
                enemy.calculateNewPosition();
                enemy.avoidBullet();
                return Avoid;
            }
            else if (enemy.iGotHit()){
                enemy.setPreviousStateChange(TimeUtils.millis());
                enemy.setTargetArea(enemy.getHideArea());
                enemy.calculateNewPosition();
                return Hide;
            }
            else if (enemy.enemyGotHit()){
                enemy.setPreviousStateChange(TimeUtils.millis());
                enemy.setTargetArea(enemy.getProtectArea());
                enemy.calculateNewPosition();
                return Protect;
            }
            return MoveRandom;
        }

        @Override
        public String getName() {
            return "MoveRandom";
        }

        @Override
        public void update(Enemy enemy, Player player) {
            System.out.println("random");
            if (enemy.getPosX() == enemy.getTargetX() && enemy.getPosY() == enemy.getTargetY()){
                enemy.calculateNewPosition();
            }
            else{
                enemy.moveEnemy();
            }
        }
    },
    //Enemy tries to attack the player
    Attack{

        @Override
        public States changeState(Enemy enemy, Player player){

            if (!enemy.playerInView(player)){
                enemy.setPreviousStateChange(TimeUtils.millis());
                enemy.setTargetArea(enemy.getFlyArea());
                enemy.calculateNewPosition();
                return MoveRandom;
            }
            else if (enemy.bulletInView()){
                enemy.setPreviousStateChange(TimeUtils.millis());
                enemy.setTargetArea(enemy.getFlyArea());
                enemy.calculateNewPosition();
                enemy.avoidBullet();
                return Avoid;
            }
            return Attack;
        }

        @Override
        public String getName() {
            return "Attack";
        }

        @Override
        public void update(Enemy enemy, Player player) {
            System.out.println("attack");
            Rectangle playerRect = new Rectangle(player.getPosX(), player.getPosY(), player.getSprite().getWidth(), player.getSprite().getHeight());
            Rectangle enemyAttackRect = new Rectangle(enemy.getPosX()-30, 0, enemy.getSprite().getWidth()+30, 1080);
            if (MathUtils.random(-100,100) > 98 && enemy.overlaps(playerRect, enemyAttackRect)){

                enemy.shoot();
            }
            enemy.moveEnemy();
        }
    },
    //Enemy tries to avoid all bullets from the player
    Avoid{

        @Override
        public States changeState(Enemy enemy, Player player){

            if (!enemy.bulletInView()){
                enemy.setPreviousStateChange(TimeUtils.millis());
                enemy.setTargetArea(enemy.getFlyArea());
                enemy.calculateNewPosition();
                return MoveRandom;
            }
            else if (enemy.iGotHit()){
                enemy.setPreviousStateChange(TimeUtils.millis());
                enemy.setTargetArea(enemy.getHideArea());
                enemy.calculateNewPosition();
                return Hide;
            }
            else if (enemy.enemyGotHit()){
                enemy.setPreviousStateChange(TimeUtils.millis());
                enemy.setTargetArea(enemy.getProtectArea());
                enemy.calculateNewPosition();
                return Protect;
            }
            return Avoid;
        }

        @Override
        public String getName() {
            return "Avoid";
        }

        @Override
        public void update(Enemy enemy, Player player) {
            System.out.println("avoid");
            //todo Set target position to area without bullets (try 3 times, else just use the last one calculated):
            if (enemy.getPosX() == enemy.getTargetX() && enemy.getPosY() == enemy.getTargetY()){
                enemy.avoidBullet();
            }
            //
            enemy.moveEnemy();
        }
    },
    //Enemy hides behind other enemies
    Hide{

        @Override
        public States changeState(Enemy enemy, Player player){

            if (enemy.bulletInView()){
                enemy.setPreviousStateChange(TimeUtils.millis());
                enemy.setTargetArea(enemy.getFlyArea());
                enemy.avoidBullet();
                return Avoid;
            }
            else if (enemy.enemyIsNearDeath(enemy)){
                enemy.setPreviousStateChange(TimeUtils.millis());
                enemy.setTargetArea(enemy.getProtectArea());
                enemy.calculateNewPosition();
                return Protect;
            }
            return Hide;
        }

        @Override
        public String getName() {
            return "Hide";
        }

        @Override
        public void update(Enemy enemy, Player player) {
            System.out.println("hide");
            enemy.moveEnemy();
        }
    },
    //Enemy tries to protect the enemies that are hiding
    Protect{

        @Override
        public States changeState(Enemy enemy, Player player){

            if (enemy.iGotHit()){
                enemy.setPreviousStateChange(TimeUtils.millis());
                enemy.setTargetArea(enemy.getHideArea());
                enemy.calculateNewPosition();
                return Hide;
            }
            else if (!enemy.enemyGotHit()){
                enemy.setPreviousStateChange(TimeUtils.millis());
                enemy.setTargetArea(enemy.getFlyArea());
                enemy.calculateNewPosition();
                return MoveRandom;
            }
            return Protect;
        }

        @Override
        public String getName() {
            return "Protect";
        }

        @Override
        public void update(Enemy enemy, Player player) {
            System.out.println("protect");
            Rectangle playerRect = new Rectangle(player.getPosX(), player.getPosY(), player.getSprite().getWidth(), player.getSprite().getHeight());
            Rectangle enemyAttackRect = new Rectangle(enemy.getPosX()-30, 0, enemy.getSprite().getWidth()+30, 1080);
            if (MathUtils.random(-100,100) > 98 && enemy.overlaps(playerRect, enemyAttackRect)){

                enemy.shoot();
            }
            enemy.moveEnemy();
        }
    };

    public abstract States changeState(Enemy enemy, Player player);
    public abstract String getName();
    public abstract void update(Enemy enemy, Player player);

}
