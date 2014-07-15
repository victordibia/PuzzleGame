/**
 *  
 * Author:  Victor Dibia 
 * Date last modified: Feb 10, 2014
 * Sample Code for Learning Cocos2D for Android 
 */
package com.example.puzzlegame;

import java.io.IOException;
import java.io.InputStream;

import org.cocos2d.actions.instant.CCCallFuncN;
import org.cocos2d.actions.interval.CCDelayTime;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCScaleBy;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.config.ccMacros;
import org.cocos2d.layers.CCColorLayer;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItemLabel;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCSpriteFrame;
import org.cocos2d.opengl.CCBitmapFontAtlas;
import org.cocos2d.opengl.CCTexture2D;
import org.cocos2d.sound.SoundEngine;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor3B;
import org.cocos2d.types.ccColor4B;
import org.cocos2d.utils.CCFormatter;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.MotionEvent;




public class PictureGameLayer extends CCLayer {
	private static final int STATUS_LABEL_TAG = 20;
	private static final int TIMER_LABEL_TAG = 21;
	private static final int MOVES_LABEL_TAG = 22;
	private static final int TILE_NODE_TAG = 23;
	private static float TILE_SQUARE_SIZE = 0;
	private static final int NUM_ROWS = 3;
	private static final int NUM_COLUMNS = 3;
	private static final int PAUSE_OVERLAY_TAG = 25;

	private static CGSize screenSize;

	private static int thetime = 0 ;
	private int toppoint = 0 ;
	CCBitmapFontAtlas statusLabel ;

	private static CGPoint emptyPosition  ;
	float generalscalefactor = 0.0f ;
	private int topleft = 0;
	private int moves = 0 ;
	private Context appcontext;
	public static boolean gameover = false ;

	public PictureGameLayer () {

		this.setIsTouchEnabled(true);
		//Add Background Sprite Image
		screenSize = CCDirector.sharedDirector().winSize();
		generalscalefactor  = CCDirector.sharedDirector().winSize().height / 500 ;
		CCSprite background = CCSprite.sprite("background.jpg");
		background.setScale(screenSize.width / background.getContentSize().width);
		background.setAnchorPoint(CGPoint.ccp(0f,1f)) ;
		background.setPosition(CGPoint.ccp(0, screenSize.height));
		addChild(background,-5);

		// Add Game Status Label
		statusLabel = CCBitmapFontAtlas.bitmapFontAtlas ("Tap Tiles to Begin", "bionic.fnt");
		statusLabel.setScale(1.3f*generalscalefactor);
		statusLabel.setAnchorPoint(CGPoint.ccp(0,1));
		statusLabel.setPosition( CGPoint.ccp( 25*generalscalefactor, screenSize.height - 10*generalscalefactor));
		addChild(statusLabel,-2, STATUS_LABEL_TAG);

		// Add Timer Label to track time
		CCBitmapFontAtlas timerLabel = CCBitmapFontAtlas.bitmapFontAtlas ("00:00", "bionic.fnt");
		timerLabel.setScale(1.5f*generalscalefactor);
		timerLabel.setAnchorPoint(1f,1f);
		timerLabel.setColor(ccColor3B.ccc3(50, 205, 50));
		timerLabel.setPosition(CGPoint.ccp(screenSize.width - 25*generalscalefactor , screenSize.height - 10*generalscalefactor ));
		addChild(timerLabel,-2,TIMER_LABEL_TAG);

		// Add Moves Label to track number of moves
		CCBitmapFontAtlas movesLabel = CCBitmapFontAtlas.bitmapFontAtlas ("Moves : 000", "bionic.fnt");
		movesLabel.setScale(0.8f*generalscalefactor);
		movesLabel.setAnchorPoint(1f,0f);
		movesLabel.setColor(ccColor3B.ccc3(50, 205, 50)); 
		movesLabel.setPosition(CGPoint.ccp(screenSize.width - 25*generalscalefactor, timerLabel.getPosition().y - timerLabel.getContentSize().height*generalscalefactor - 10*generalscalefactor -  timerLabel.getContentSize().height*generalscalefactor));
		addChild(movesLabel,-2,MOVES_LABEL_TAG);

		schedule("updateTimeLabel", 1.0f);
		generateTiles();

		//add a back button to main menu
		CCBitmapFontAtlas label = CCBitmapFontAtlas.bitmapFontAtlas("BACK", "bionic.fnt");
		CCMenuItemLabel item5 = CCMenuItemLabel.item(label, this, "menuCallback");
		item5.setScale(1.5f *generalscalefactor);
		CCMenu backemenu = CCMenu.menu(item5); 
		backemenu.setPosition(CGPoint.make(screenSize.width - item5.getContentSize().width * 1.5f *generalscalefactor , item5.getContentSize().height * 1.5f *generalscalefactor));
		
		addChild(backemenu, 300) ;

	}

	public void updateTimeLabel(float dt) {
		thetime  += 1;
		String string = CCFormatter.format("%02d:%02d", (int)(thetime /60) , (int)thetime % 60 );
		CCBitmapFontAtlas timerLabel = (CCBitmapFontAtlas) getChildByTag(TIMER_LABEL_TAG) ;
		timerLabel.setString(string);

	}

	public static CCScene scene()
	{
		CCScene scene = CCScene.node();
		CCLayer layer = new PictureGameLayer();
		scene.addChild(layer);
		return scene;
	}

	public void generateTiles(){

		//We create a Node element to hold all our tiles
		CCNode tilesNode = CCNode.node();
		tilesNode.setTag(TILE_NODE_TAG);
		addChild(tilesNode); 		
		float scalefactor ;   // a value we compute to help scale our tiles
		int useableheight  ;	
		int tileIndex = 0 ;

		//We attempt to calculate the right size for the tiles given the screen size and 
		//space left after adding the status label at the top
		int nextval ;

		int[] tileNumbers = {5,1,2,8,7,6,0,4,3};  //random but solvable sequence of numbers

		//TILE_SQUARE_SIZE = (int) ((screenSize.height  *generalscalefactor)/NUM_ROWS) ;
		int useablewidth = (int) (screenSize.width - statusLabel.getContentSize().width*generalscalefactor ) ;
		useableheight =  (int) (screenSize.height  - 40*generalscalefactor - statusLabel.getContentSize().height * 1.3f*generalscalefactor) ;

		TILE_SQUARE_SIZE = (int) Math.min((useableheight/NUM_ROWS) , (useablewidth/NUM_COLUMNS)) ;
		
		ccMacros.CCLOG("Began", " Scale Fact " + generalscalefactor + " :  "  );

		
		toppoint = (int) (useableheight  - (TILE_SQUARE_SIZE / 2) + 30*generalscalefactor)   ;
		scalefactor = TILE_SQUARE_SIZE / (150.0f*generalscalefactor) ;

		topleft = (int) ((TILE_SQUARE_SIZE / 2*scalefactor) + 30*generalscalefactor) ;

		CCSprite tile = CCSprite.sprite("tile.png");
		//CCSprite tilebox = CCSprite.sprite("tilebox.png");

		for (int j = toppoint ; j > toppoint - (TILE_SQUARE_SIZE * NUM_ROWS); j-= TILE_SQUARE_SIZE){
			for (int i = topleft ; i < (topleft - 5*generalscalefactor) + (TILE_SQUARE_SIZE * NUM_COLUMNS); i+= TILE_SQUARE_SIZE){

				if (tileIndex >= (NUM_ROWS * NUM_COLUMNS)) {
					break ;
				}
				nextval = tileNumbers[tileIndex ];
				CCNodeExt eachNode =  new  CCNodeExt(); 
				eachNode.setContentSize(tile.getContentSize());
				eachNode.setScale(scalefactor);
				//
				//Layout Node based on calculated postion
				eachNode.setPosition(i, j);
				eachNode.setNodeText(nextval + "");

				//Add Tile number
				CCBitmapFontAtlas tileNumber = CCBitmapFontAtlas.bitmapFontAtlas ("00", "bionic.fnt");
				tileNumber.setScale(2.0f * scalefactor);
				tileNumber.setString(nextval + "");  // add tile number to keep track of it
				eachNode.addChild(tileNumber,2 );



				if( nextval != 0){
					tilesNode.addChild(eachNode,1,nextval);
				}else {
					emptyPosition = CGPoint.ccp(i, j);
				}


				tileIndex++;
			}
		} 

		//Add Picture Sprites as tile background 

		CCNode tileNode = (CCNode) getChildByTag(TILE_NODE_TAG);
		int nodeindex = 1 ;
		CCTexture2D metexture = new CCTexture2D();
		Bitmap mybit = null;
		mybit = MainActivity.bitmap; 

		metexture.initWithImage(mybit);


		for (float j = 0 ; j < NUM_ROWS ; j++){
			for (float i = 0 ; i <NUM_COLUMNS; i++){

				//Calculate the size of each tile by dividing the height and width of our image
				// and returning the min value of both calculations
				float theblock = Math.min( mybit.getHeight()/NUM_ROWS, mybit.getWidth()/NUM_COLUMNS) ;

				//Create a new sprite using this dimension above and from a given portion of the image
				CCSpriteFrame myframe = CCSpriteFrame.frame(metexture, CGRect.make(i*theblock, j*theblock, theblock, theblock), CGPoint.make(0, 0));
				tile = CCSprite.sprite(myframe);
				tile.setScale((TILE_SQUARE_SIZE/theblock) * (1.2f*scalefactor));

				//Assign our newly created sprite background to a node created earlier.
				tileNode.getChildByTag(nodeindex).addChild(tile,-1,1); 
				tileNode.setContentSize(tile.getContentSize());

				if(nodeindex == (NUM_ROWS * NUM_COLUMNS) - 1){
					break ;
				} 
				nodeindex++ ;
			}
		}


	}


	@Override
	public boolean ccTouchesBegan(MotionEvent event)
	{
		//Get touch location cordinates
		CGPoint location = CCDirector.sharedDirector().convertToGL(CGPoint.ccp(event.getX(), event.getY()));
		CGRect spritePos ;

		CCNode tilesNode = (CCNode) getChildByTag(TILE_NODE_TAG) ;
		//ccMacros.CCLOG("Began", "Began : " + location.x + " :  "  );

		//We loop through each of the tiles and get its cordinates
		for (int i = 1 ; i < (NUM_ROWS * NUM_COLUMNS); i++){
			CCNodeExt eachNode = (CCNodeExt) tilesNode.getChildByTag(i) ;

			//we construct a rectangle covering the current tiles cordinates
			spritePos = CGRect.make(
					eachNode.getPosition().x - (eachNode.getContentSize().width*generalscalefactor/2.0f),
					eachNode.getPosition().y - (eachNode.getContentSize().height*generalscalefactor/2.0f),
					eachNode.getContentSize().width*generalscalefactor   ,
					eachNode.getContentSize().height*generalscalefactor   );
			//Check if the user's touch falls inside the current tiles cordinates
			if(spritePos.contains(location.x, location.y)){
				//ccMacros.CCLOG("Began Touched Node", "Began touched : " + eachNode.getNodeText());
				slideCallback(eachNode); // if yes, we pass the tile for sliding.

			}
		}

		return true ;
	}

	public void slideCallback(CCNodeExt thenode) {

		CGPoint nodePosition = thenode.getPosition();

		//Determine the position to slide the tile to .. ofcourse only if theres an empty space beside it

		if((nodePosition.x - TILE_SQUARE_SIZE)== emptyPosition.x && nodePosition.y == emptyPosition.y){
			slideTile("Left", thenode,true);
		}else if((nodePosition.x + TILE_SQUARE_SIZE) == emptyPosition.x && nodePosition.y == emptyPosition.y){
			slideTile("Right", thenode,true);
		}else if((nodePosition.x)== emptyPosition.x && nodePosition.y == (emptyPosition.y  + TILE_SQUARE_SIZE )){
			slideTile("Down", thenode,true);
		}else if((nodePosition.x )== emptyPosition.x && nodePosition.y == (emptyPosition.y  - TILE_SQUARE_SIZE)){ 
			slideTile("Up", thenode,true);
		}else{
			slideTile("Unmovable", thenode,false);
		}

	}

	public void slideTile(String direction, CCNodeExt thenode, boolean move){ 
		CCBitmapFontAtlas moveslabel = (CCBitmapFontAtlas) getChildByTag(MOVES_LABEL_TAG);

		if(move && !gameover){ 
			//  Increment the moves label and animate the tile
			moves ++ ;
			moveslabel.setString("Moves : " + CCFormatter.format("%03d", moves ));

			//Update statuslabel
			statusLabel.setString("Tile : " + thenode.getNodeText() + " -> " + direction);

			//Animate the tile to slide it
			CGPoint nodePosition = thenode.getPosition();
			CGPoint tempPosition = emptyPosition ;
			CCMoveTo movetile = CCMoveTo.action(0.4f, tempPosition); 
			CCSequence movetileSeq = CCSequence.actions(movetile, CCCallFuncN.action(this, "handleWin"));
			thenode.runAction(movetileSeq);
			emptyPosition = nodePosition ;

			//Play a sound 
			appcontext = CCDirector.sharedDirector().getActivity();
			SoundEngine.sharedEngine().playEffect(appcontext, R.raw.tileclick);
			thenode.runAction(movetileSeq); 
		}else{ 
		}

	}


	public void handleWin(Object sender){
		if(checkCorrect()){
			//gameover = true ;

			SoundEngine.sharedEngine().playEffect(appcontext, R.raw.cheer);

			WinCallback(sender);
		}
	}

	//This method checks if the puzzle has been correctly solved.
	public boolean checkCorrect(){
		CCNode tileNode = (CCNode) getChildByTag(TILE_NODE_TAG);
		int nodeindex = 1 ;
		boolean result = false;

		for (float j = toppoint ; j > toppoint - (TILE_SQUARE_SIZE * NUM_ROWS); j-= TILE_SQUARE_SIZE){
			for (float i = topleft ; i < (topleft - 5) + (TILE_SQUARE_SIZE * NUM_COLUMNS); i+= TILE_SQUARE_SIZE){
				if(tileNode.getChildByTag(nodeindex).getPosition().x == i && tileNode.getChildByTag(nodeindex).getPosition().y == j ){
					result = true ; 
				}else{ 
					return false ;
				}
				nodeindex++ ;
				if(nodeindex == (NUM_ROWS * NUM_COLUMNS)){
					return result ;
				}
			}

			//rowindex++ ;
		}

		return result ;
	}


	public void WinCallback(Object sender) {
		unschedule("updateTimeLabel"); // stop the timer

		//Create a dark semi-transparent layer called pauseOverlay and add over our scene
		CCColorLayer pauseOverlay = CCColorLayer.node(ccColor4B.ccc4(25, 25, 25, 255)); 
		pauseOverlay.setOpacity(200);
		pauseOverlay.setIsTouchEnabled(true); 
		addChild(pauseOverlay,200,PAUSE_OVERLAY_TAG);

		//Show the number of moves in a label called movesLabel
		CCBitmapFontAtlas gamemoves = CCBitmapFontAtlas.bitmapFontAtlas ( CCFormatter.format("%02d", moves ) + " Moves", "bionic.fnt");
		gamemoves.setAnchorPoint(CGPoint.ccp(0,1)); 
		gamemoves.setScale(generalscalefactor);
		gamemoves.setAnchorPoint(0.5f,1f);

		//Annimate the moves label a little .. scale it
		gamemoves.setPosition(CGPoint.ccp(screenSize.width / 2.0f , screenSize.height/2.0f ));
		pauseOverlay.addChild(gamemoves,300 );
		gamemoves.runAction(CCSequence.actions(
				CCDelayTime.action(0.5f),
				CCScaleBy.action(0.2f, 2.0f)
				));

		//Some instruction for the user to procee
		CCBitmapFontAtlas instructionFontAtlas = CCBitmapFontAtlas.bitmapFontAtlas( "TAP Back button below to continue!" , "bionic.fnt");
		instructionFontAtlas.setPosition(gamemoves.getPosition().x, -instructionFontAtlas.getContentSize().height*0.6f*generalscalefactor) ;
		instructionFontAtlas.setScale(0.6f*generalscalefactor) ;
		pauseOverlay.addChild(instructionFontAtlas,301);

		//animate the instruction label a little ... moveTo
		instructionFontAtlas.runAction(CCSequence.actions(
				CCDelayTime.action(0.5f), 
				CCMoveTo.action(0.5f, CGPoint.make(gamemoves.getPosition().x, gamemoves.getPosition().y - 10*generalscalefactor - gamemoves.getContentSize().height *generalscalefactor * 2.0f)) 
				));

		//Show amount of time in a lable
		CCBitmapFontAtlas gametime = CCBitmapFontAtlas.bitmapFontAtlas (CCFormatter.format("%02d:%02d", (int)(thetime /60) , (int)thetime % 60 ), "bionic.fnt");
		gametime.setAnchorPoint(CGPoint.ccp(0,1)); 
		gametime.setScale(generalscalefactor);
		gametime.setAnchorPoint(0.5f,1f);
		gametime.setPosition(CGPoint.ccp(screenSize.width / 2.0f , gamemoves.getPosition().y + gamemoves.getContentSize().height*generalscalefactor/2.0f + 10 ));
		pauseOverlay.addChild(gametime,301);


		//reset time to zero



		CCBitmapFontAtlas label = CCBitmapFontAtlas.bitmapFontAtlas("BACK", "bionic.fnt");
		CCMenuItemLabel item5 = CCMenuItemLabel.item(label, this, "backCallback");

		CCMenu resumemenu = CCMenu.menu(item5); 
		resumemenu.setPosition(CGPoint.make(label.getContentSize().width, label.getContentSize().width));
		pauseOverlay.addChild(resumemenu,800) ;

	}

	public void backCallback(Object sender) {
		schedule("updateTimeLabel"); //restart timer
		moves = 0 ; //Reset moves
		thetime = 0 ; //Reset time

		//Remove the pause layer and reload the scene
		CCColorLayer pauselayer = (CCColorLayer) getChildByTag(PAUSE_OVERLAY_TAG) ;
		pauselayer.runAction(CCMoveTo.action(0.2f, CGPoint.make(screenSize.width / 2.0f, screenSize.height + pauselayer.getContentSize().height*generalscalefactor)));

		pauselayer.removeAllChildren(true);
		pauselayer.removeSelf() ;
		CCDirector.sharedDirector().replaceScene(PictureGameLayer.scene());

	}

	public static void getBitMapfromCamera() {

		// for camera intent use  
		Intent intent = new Intent( android.provider.MediaStore.ACTION_IMAGE_CAPTURE); 
		intent.setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP) ; 
		MainActivity.app.startActivityForResult(intent,MainActivity.CAMERA_REQUEST_CODE);	 

	}

	public static void getBitMapFromGallery() {

		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);  
		intent.setType("image/*"); 
		intent.setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP) ;
		intent.addCategory(Intent.CATEGORY_OPENABLE);	       
		MainActivity.app.startActivityForResult(intent,MainActivity.GALLERY_REQUEST_CODE); 
	}
	public static void getBitmapFromAsset(String strName) throws IOException
	{
		AssetManager assetManager = CCDirector.sharedDirector().getActivity().getAssets();

		InputStream istr = assetManager.open(strName);
		Bitmap bitmap = BitmapFactory.decodeStream(istr);

		MainActivity.bitmap =  bitmap;
	}
	public void menuCallback(Object sender) {

		CCDirector.sharedDirector().replaceScene(SlidingMenuLayer.scene());

	}


}


