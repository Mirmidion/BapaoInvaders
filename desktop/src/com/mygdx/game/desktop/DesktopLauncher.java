package com.mygdx.game.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.PixmapIO;
import com.mygdx.game.BapoaInvaders;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Driver;


public class DesktopLauncher {
	public static void main(String[] args) {


		String url = "jdbc:mysql://localhost/cursus";
		String username = "root", password = "";

		try{
			Connection connection = DriverManager.getConnection(url,username,password);
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("SELECT naam FROM medewerker");
			//int gelukt = statement.executeUpdate("INSERT INTO cursus VALUES(‘JAV’, ‘Java’,’S’,5)");
			while(rs.next()){
				System.out.println(rs.getString(1));
			}
			statement.close(); //sluit ook de resultset
			connection.close();
		}
		catch(Exception e){
			System.out.println(e);
		}


		
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		config.title = "Bapao Invaders";
		config.addIcon("Icon.png", Files.FileType.Internal);
		config.width = LwjglApplicationConfiguration.getDesktopDisplayMode().width;
		config.height = LwjglApplicationConfiguration.getDesktopDisplayMode().height;
		config.x = -7;
		config.y = 0;

		new LwjglApplication(new BapoaInvaders(), config);

	}
}
