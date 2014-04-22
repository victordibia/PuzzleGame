/**
 *  
 * Author:  Victor Dibia 
 * Date last modified: Feb 10, 2014
 * Sample Code for Learning Cocos2D for Android 
 */
package com.example.puzzlegame;

import java.io.IOException;

import org.cocos2d.actions.interval.CCDelayTime;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.config.ccMacros;
import org.cocos2d.extensions.scroll.CCClipNode;
import org.cocos2d.extensions.scroll.CCScrollView;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.opengl.CCBitmapFontAtlas;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;

import android.view.MotionEvent;


public class SlidingMenuLayer extends CCLayer {
	private static final int SCROLLVIEW_TAG = 300;
	private static CGSize screenSize;
	CCScrollView scrollView;
	CCBitmapFontAtlas statusLabel ;
	private CGPoint startlocation; //keep track of touch starting point
	private CGPoint endlocation;   ////keep track of touch ending point
	float tilescale ;

	float generalscalefactor = 0.0f ;


	public SlidingMenuLayer () {

		this.setIsTouchEnabled(true);
		this.setIsKeyEnabled(true) ; 
		this.isTouchEnabled_ = true ;


		screenSize = CCDirector.sharedDirector().winSize();
		generalscalefactor  = CCDirector.sharedDirector().winSize().height / 500 ;

		CCSprite background = CCSprite.sprite("background.jpg");
		background.setScale(screenSize.width / background.getContentSize().width);
		background.setAnchorPoint(CGPoint.ccp(0f,1f)) ;
		background.setPosition(CGPoint.ccp(0, screenSize.height));
		addChild(background,-5);

		//Some instruction for the user to process
		CCBitmapFontAtlas instructionFontAtlas = CCBitmapFontAtlas.bitmapFontAtlas( "Select an option" , "bionic.fnt");
		instructionFontAtlas.setPosition(screenSize.width / (2.0f*generalscalefactor) , screenSize.height) ;
		instructionFontAtlas.setScale(0.8f*generalscalefactor) ;
		addChild(instructionFontAtlas,301);

		//animate the instruction label a little ... moveTo
		instructionFontAtlas.runAction(CCSequence.actions(
				CCDelayTime.action(0.4f), 
				CCMoveTo.action(0.5f, CGPoint.make(screenSize.width / (2.0f*generalscalefactor) , screenSize.height -  60 *generalscalefactor )) 
				));

		// Create our menu ttitles

		scrollView = CCScrollView.view(CGSize.zero()); 
		scrollView.bounces = true ;  //the bounce effect when a user scrolls to the end
		scrollView.setClipToBounds( true) ;  // 
		scrollView.direction =1 ;   // for horizontal scrolling.
		addChild(scrollView,218, SCROLLVIEW_TAG); 

		String[] menutitles = {"number puzzle", "picture puzzle", "camera puzzle" , "gallery puzzle" , "fixed menu"  } ;
		CCSprite tilebox = CCSprite.sprite("picture.png");	;
		float newwidth = tilebox.getContentSize().width * 1.5f * generalscalefactor ; 
		tilescale = 1.5f * generalscalefactor ;

		for (int i=0 ; i < menutitles.length ; i++){
			//A meu image sprite
			tilebox = CCSprite.sprite("picture.png");				
			tilebox.setAnchorPoint(0.5f, 0.5f);
			tilebox.setScale(tilescale);
			// Each one is placed with a 30 pixel space from the next ... i *30*generalscalefactor.
			tilebox.setPosition(CGPoint.ccp((i*newwidth) + 30*generalscalefactor + i*30*generalscalefactor , screenSize.height/2 - ((tilebox.getContentSize().height *tilescale)/2.0f) ));


			//a text title added below each menu image with same x cordinates
			CCBitmapFontAtlas nlabel = CCBitmapFontAtlas.bitmapFontAtlas(menutitles[i], "bionic.fnt");
			nlabel.setScale(0.8f * generalscalefactor);
			nlabel.setPosition(CGPoint.ccp((i*newwidth) + 30*generalscalefactor + i*30*generalscalefactor , tilebox.getPosition().y - ((tilebox.getContentSize().height *tilescale)/2.0f) + 10*generalscalefactor));
			scrollView.addChild(tilebox,1,i);
			scrollView.addChild(nlabel);
		}



		// You need to set contentSize to enable scrolling.

		// The scrollview is like a sliding window that shows portions of a long list
		//View Size is the size of the window and Content size is the size of the entire long list
		// For scrolling to occur, the content lenght must be greater than the viewsize
		// Thus you must set both content and view size.

		scrollView.setViewSize(CGSize.make(screenSize.width, screenSize.height ));
		scrollView.setContentSize(CGSize.make(newwidth* (menutitles.length + 1), screenSize.height )); 



	}

	public static CCScene scene()
	{
		CCScene scene = CCScene.node();
		CCLayer layer = new SlidingMenuLayer();
		scene.addChild(layer);
		return scene;
	}

	//Controls the events launched when a menu item is clicked
	private void launchmenu( int i){
		if (i == 0){
			CCScene scene =  GameLayer.scene(); //  
			CCDirector.sharedDirector().runWithScene(scene); 
		}else if (i == 1){
			try {
				PictureGameLayer.getBitmapFromAsset("benin.jpg");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			CCScene scene =  PictureGameLayer.scene(); //  
			CCDirector.sharedDirector().runWithScene(scene); 
		}
		else if (i == 2){
			PictureGameLayer.getBitMapfromCamera(); 
		}
		else if (i == 3){
			PictureGameLayer.getBitMapFromGallery();; 
		}
		else if (i == 4){
			CCScene scene =  MenuLayer.scene(); //  
			CCDirector.sharedDirector().runWithScene(scene); 
		}
	}

	@Override
	public boolean ccTouchesBegan(MotionEvent event) {

		startlocation = CCDirector.sharedDirector().convertToGL(CGPoint.ccp(event.getX(), event.getY()));

		//ccMacros.CCLOG("Dist Bdgan ","------------------ " );

		return true;
	}

	@Override
	public boolean ccTouchesEnded(MotionEvent event)
	{

		CGRect spritePos ; 

		endlocation = CCDirector.sharedDirector().convertToGL(CGPoint.ccp(event.getX(), event.getY()));

		if (Math.abs(startlocation.x - endlocation.x) < 5){

			//Obtain a reference to the scrollview control
			CCScrollView tilesNode = (CCScrollView)getChildByTag(SCROLLVIEW_TAG) ;

			//Get scrollview children .. its inside container variable
			CCClipNode container = (CCClipNode)(tilesNode.getChildren().get(0)) ;  

			//Iterate through its children ... nlabel and tilebox sprite			
			// we divide by two here because we have two items added for each menu item
			for (int i = 0 ; i < ((container.getChildren().size())/2)  ; i++){ 

				// In order to get the position of the item menu that has been touched we note the following
				// We need the current position of the touch event ... which is endlocation .. check
				// We need to know where it falls into the scrollview content
				// Since we are scrolling horizontally we add the content current x position to our touch x position
				//and construct a rectangle in that area . 
				// You can inspect these values in logcat (x, y, endlocation ) using  ccMacros.CCLOG.
				CCSprite backsprite = (CCSprite)container.getChildByTag(i);
				spritePos = CGRect.make(
						(backsprite.getPosition().x + container.getPosition().x ) ,
						backsprite.getPosition().y ,
						backsprite.getContentSize().width*tilescale  ,
						backsprite.getContentSize().height*tilescale  );
				if(spritePos.contains(endlocation.x, endlocation.y)){

					//Use cccMacro to log information to logcat to to LogCat
					ccMacros.CCLOG("Tile " + i + "  has been touched : ", "Began touched : " + i);
					//Launch the appropriate menu when its toucheds
					launchmenu(i);
				}
			}

		} 
		return true ;
	}

}


