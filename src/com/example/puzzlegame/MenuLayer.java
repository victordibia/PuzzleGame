/**
	 *  
	 * Author:  Victor Dibia 
	 * Date last modified: Feb 10, 2014
	 * Sample Code for Learning Cocos2D for Android 
	 */
package com.example.puzzlegame;

import org.cocos2d.actions.interval.CCDelayTime;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.menus.CCMenuItemLabel;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;
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

		CCSprite background = CCSprite.sprite("background.jpg");
		background.setScale(screenSize.width / background.getContentSize().width);
		background.setAnchorPoint(CGPoint.ccp(0f,1f)) ;
		background.setPosition(CGPoint.ccp(0, screenSize.height));
		addChild(background,-5);




		CCBitmapFontAtlas label = CCBitmapFontAtlas.bitmapFontAtlas("Number", "bionic.fnt");
		CCMenuItemLabel lableitem = CCMenuItemLabel.item(label, this, "numberCallback");
		lableitem.setScale(1.4f *generalscalefactor);
		CCMenu resumemenu = CCMenu.menu(lableitem); 
		resumemenu.setPosition(CGPoint.make(screenSize.width / 3* generalscalefactor, screenSize.height/2*generalscalefactor));
		addChild(resumemenu,800) ;

		//Add Picture based menu button
		CCMenuItemImage backbtn = CCMenuItemImage.item("picture2.png", "picture2.png",this, "pictureCallback");
		backbtn.setScale(1.6f *generalscalefactor);

		CCMenu backmenu = CCMenu.menu(backbtn); 
		backmenu.setContentSize(backbtn.getContentSize()); 
		backmenu.setPosition(CGPoint.make(0, 0));
		backbtn.setPosition(CGPoint.make(resumemenu.getPosition().x + label.getContentSize().width*1.4f *generalscalefactor + backbtn.getContentSize().width/2 * generalscalefactor, screenSize.height/2*generalscalefactor ));

		addChild(backmenu, 100);


		//Some instruction for the user to procee
		CCBitmapFontAtlas instructionFontAtlas = CCBitmapFontAtlas.bitmapFontAtlas( "Select Your Puzzle Type !" , "bionic.fnt");
		instructionFontAtlas.setPosition(screenSize.width / (2.0f*generalscalefactor) , -screenSize.height/2
				) ;
		instructionFontAtlas.setScale(0.8f*generalscalefactor) ;
		addChild(instructionFontAtlas,301);

		//animate the instruction label a little ... moveTo
		instructionFontAtlas.runAction(CCSequence.actions(
				CCDelayTime.action(0.4f), 
				CCMoveTo.action(0.5f, CGPoint.make(screenSize.width / (2.0f*generalscalefactor) , screenSize.height/2*generalscalefactor + 60 *generalscalefactor )) 
				));
		//add a back button to main menu
				CCBitmapFontAtlas menulabel = CCBitmapFontAtlas.bitmapFontAtlas("BACK", "bionic.fnt");
				CCMenuItemLabel item5 = CCMenuItemLabel.item(menulabel, this, "menuCallback");

				CCMenu backemenu = CCMenu.menu(item5); 
				backemenu.setPosition(CGPoint.make(screenSize.width - label.getContentSize().width * 1.4f *generalscalefactor, label.getContentSize().width *1.4f *generalscalefactor));
				addChild(backemenu, 300) ;
	}

	public static CCScene scene()
	{
		CCScene scene = CCScene.node();
		CCLayer layer = new MenuLayer();
		scene.addChild(layer);
		return scene;
	}


	public void numberCallback(Object sender) {
		CCScene scene =  GameLayer.scene(); //  
		CCDirector.sharedDirector().runWithScene(scene); 

	}

	public void pictureCallback(Object sender) {
		CCScene scene =  PictureGameLayer.scene(); //  
		CCDirector.sharedDirector().runWithScene(scene); 

	}
	
	public void menuCallback(Object sender) {

		CCDirector.sharedDirector().replaceScene(SlidingMenuLayer.scene());

	}
}


