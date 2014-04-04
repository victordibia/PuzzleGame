package com.example.puzzlegame;

import org.cocos2d.actions.interval.CCDelayTime;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItemLabel;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.opengl.CCBitmapFontAtlas;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize; 


public class MenuLayer extends CCLayer {
	private static CGSize screenSize;

	CCBitmapFontAtlas statusLabel ;

	float generalscalefactor = 0.0f ;
	public static boolean gameover = false ;

	public MenuLayer () {

		this.setIsTouchEnabled(true);
		 

		screenSize = CCDirector.sharedDirector().winSize();
		generalscalefactor  = CCDirector.sharedDirector().winSize().height / 500 ;
		
		//Some instruction for the user to procee
		CCBitmapFontAtlas instructionFontAtlas = CCBitmapFontAtlas.bitmapFontAtlas( "Select Your Puzzle Type !" , "bionic.fnt");
		instructionFontAtlas.setPosition(screenSize.width / (2.0f*generalscalefactor) , 0) ;
		instructionFontAtlas.setScale(0.6f*generalscalefactor) ;
		addChild(instructionFontAtlas,301);

		//animate the instruction label a little ... moveTo
		instructionFontAtlas.runAction(CCSequence.actions(
				CCDelayTime.action(0.5f), 
				CCMoveTo.action(0.5f, CGPoint.make(screenSize.width / (2.0f*generalscalefactor) , 10 *generalscalefactor)) 
				));
 	
		 
		 CCBitmapFontAtlas label = CCBitmapFontAtlas.bitmapFontAtlas("Number", "bionic.fnt");
		 CCMenuItemLabel item5 = CCMenuItemLabel.item(label, this, "backCallback");

		  CCMenu resumemenu = CCMenu.menu(item5); 
		resumemenu.setPosition(CGPoint.make(label.getContentSize().width, label.getContentSize().width));
	    addChild(resumemenu,800) ;

	}
	
	public static CCScene scene()
	{
		CCScene scene = CCScene.node();
		CCLayer layer = new MenuLayer();
		scene.addChild(layer);
		return scene;
	}


	public void backCallback(Object sender) {
		CCScene scene =  GameLayer.scene(); //  
		CCDirector.sharedDirector().runWithScene(scene); 

	}
}


