package experience.gallery.quote;

import java.io.File;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.Animation.Status;
import javafx.scene.Node;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import main.Experience;
import main.ExperienceController;
import main.InteractiveWall;
import main.Util;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Gesture;
import com.leapmotion.leap.GestureList;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.HandList;
import com.leapmotion.leap.Listener;
import com.leapmotion.leap.SwipeGesture;
import com.leapmotion.leap.Vector;

public class QuoteGalleryExperience extends Listener implements Experience {
	Controller controller;
	ExperienceController myController;
	StackPane pane;

	Pane canvas;

	Timeline sleepTimer;
	Timeline changeGalleryR;
	Timeline changeGalleryL;
	Timeline rightMainExit;
	Timeline leftMainExit;
	Timeline upArrowR;
	Timeline upArrowL;
	Timeline downArrowR;
	Timeline downArrowL;
	Timeline fadePlay;
	Timeline fadeStop;

	Hand right;
	Hand left;

	ImageView rightHand;
	ImageView leftHand;
	ImageView quoteView;
	ImageView artView;
	ImageView upA;
	ImageView downA;

	AnimationTimer drawHands;
	AnimationTimer changeQuotes;
	AnimationTimer changeArrows;

	double rightHandPosX = -50.0;
	double rightHandPosY = -50.0;
	double realRightHandPosX = -50.0;
	double realRightHandPosY = -50.0;

	double leftHandPosX = -50.0;
	double leftHandPosY = -50.0;
	double realLeftHandPosX = -50.0;
	double realLeftHandPosY = -50.0;

	int quoteHolder = 1;
	boolean direction;

	MediaPlayer confirmComplete;
	private File confirm = new File("media/confirmComplete.mp3");
	private final String CONFIRM_URL = confirm.toURI().toString();

	Image artButtonHovered = new Image(
			"media/galleryButton-selected 270_63 px.png", 270, 63, false, true);
	Image artButton = new Image("media/galleryButton 270_63 px.png", 270, 63,
			false, true);
	Image quoteButton = new Image("media/quoteButton-selected 270_63 px.png",
			270, 63, false, true);
	Image quoteButtonUnselected = new Image("media/quoteButton270_63 px.png",
			270, 63, false, true);

	Image upArrow = new Image("media/up arrow 200_80.png", 200, 80, false, true);
	Image upArrowHovered = new Image("media/up arrow_hover.png", 200, 80,
			false, true);
	Image downArrow = new Image("media/down arrow 200_80.png", 200, 80, false,
			true);
	Image downArrowHovered = new Image("media/down arrow_hover.png", 200, 80,
			false, true);

	ExperienceQuote[] quoteList;

	Text topQ;
	Text mainQ;
	Text bottomQ;

	public QuoteGalleryExperience() {
		quoteList = new ExperienceQuote[] {
				new ExperienceQuote(
						"Life isn't about finding yourself. Life is about creating yourself.",
						"George Bernard Shaw"),
				new ExperienceQuote(
						"In the end, it's not the years in your life that count. It's the life in your years.",
						"Abraham Lincoln"),
				new ExperienceQuote(
						"You have succeeded in life when all you really want is only what you really need.",
						"Vernon Howard"),
				new ExperienceQuote(
						"Open your eyes, look within. Are you satisfied with the life you're living?",
						"Bob Marley"),
				new ExperienceQuote(
						"It is often said that before you die your life passes before your eyes. It is in fact true. It's called living.",
						"Terry Pratchett"),
				new ExperienceQuote(
						"Success is not final, failure is not fatal: it is the courage to continue that counts.",
						"Winston Churchill"),
				new ExperienceQuote(
						"The starting point of all achievement is desire.",
						"Napoleon Hill"),
				new ExperienceQuote(
						"A successful man is one who can lay a firm foundation with the bricks others have thrown at him.",
						"David Brinkley"),
				new ExperienceQuote(
						"Immature love says: 'I love you because I need you.' Mature love says 'I need you because I love you.",
						"Erich Fromm"),
				new ExperienceQuote(
						"The most powerful weapon on earth is the human soul on fire.",
						"Ferdinand Foch"),
				new ExperienceQuote(
						"Love doesn't make the world go 'round. Love is what makes the ride worthwhile.",
						"Franklin P. Jones") };

		pane = new StackPane();
		canvas = new Pane();

		artView = new ImageView(artButton);
		artView.setLayoutX(517);
		artView.setLayoutY(849);
		canvas.getChildren().add(artView);

		quoteView = new ImageView(quoteButton);
		quoteView.setLayoutX(812);
		quoteView.setLayoutY(849);
		canvas.getChildren().add(quoteView);

		Image backImg = new Image("media/background_gallery.png", 1600, 1000,
				true, true);
		ImageView backView = new ImageView(backImg);
		backView.setPreserveRatio(true);
		pane.getChildren().add(backView);

		Image exitImg = new Image("media/Exit180_180.png", 150, 150, true, true);
		Image exitHoveredImg = new Image("media/ExitHovered180_180.png", 150,
				150, true, true);
		ImageView exitView = new ImageView(exitImg);
		exitView.setOpacity(0.3);
		exitView.setPreserveRatio(true);
		exitView.setLayoutX(1353);
		exitView.setLayoutY(785);
		canvas.getChildren().add(exitView);

		bottomQ = new Text(quoteList[quoteHolder - 1].getQuote() + "\n- "
				+ quoteList[quoteHolder - 1].getAuthor());

		mainQ = new Text(quoteList[quoteHolder].getQuote() + "\n- "
				+ quoteList[quoteHolder].getAuthor());

		topQ = new Text(quoteList[quoteHolder + 1].getQuote() + "\n- "
				+ quoteList[quoteHolder + 1].getAuthor());

		bottomQ.setFont(Font.font("Avenir", FontWeight.NORMAL, 40));
		bottomQ.setEffect(new GaussianBlur());
		bottomQ.setOpacity(0.3);

		mainQ.setFont(Font.font("Avenir", FontWeight.NORMAL, 50));

		topQ.setFont(Font.font("Avenir", FontWeight.NORMAL, 40));
		topQ.setEffect(new GaussianBlur());
		topQ.setOpacity(0.3);

		TextFlow bottomFlow = new TextFlow();
		bottomFlow.setMaxSize(800, 400);
		bottomFlow.getChildren().addAll(bottomQ);
		bottomFlow.setLayoutX(400);
		bottomFlow.setLayoutY(650);

		TextFlow mainFlow = new TextFlow();
		mainFlow.setMaxSize(1200, 400);
		mainFlow.getChildren().addAll(mainQ);
		mainFlow.setLayoutX(200);
		mainFlow.setLayoutY(350);

		TextFlow topFlow = new TextFlow();
		topFlow.setMaxSize(800, 400);
		topFlow.getChildren().addAll(topQ);
		topFlow.setLayoutX(400);
		topFlow.setLayoutY(50);

		canvas.getChildren().addAll(bottomFlow, mainFlow, topFlow);

		upA = new ImageView(upArrow);
		upA.setLayoutX(700);
		upA.setLayoutY(70);
		canvas.getChildren().add(upA);

		downA = new ImageView(downArrow);
		downA.setLayoutX(700);
		downA.setLayoutY(680);
		canvas.getChildren().add(downA);

		sleepTimer = new Timeline(new KeyFrame(Duration.millis(20000),
				ae -> goToMainMenu()));

		Image palmRightNormal = new Image("media/palmRight.png", 100, 100,
				true, true);
		rightHand = new ImageView(palmRightNormal);

		Image palmLeftNormal = new Image("media/palmLeft.png", 100, 100, true,
				true);
		leftHand = new ImageView(palmLeftNormal);

		Image rightHandFull = new Image("media/Hold_fullHand_102_107.png", 100,
				100, true, true);

		Image leftHandFull = new Image("media/HoldLeft_fullHand_102_107.png",
				100, 100, true, true);

		Media confirmMedia = new Media(CONFIRM_URL);
		confirmComplete = new MediaPlayer(confirmMedia);
		confirmComplete.setVolume(.25);

		rightMainExit = new Timeline(new KeyFrame(Duration.seconds(.5), ae -> {
			rightHand.setImage(rightHandFull);
			confirmComplete.stop();
			confirmComplete.play();
		}), new KeyFrame(Duration.seconds(1), ae -> {
			goToMainMenu();
		}), new KeyFrame(Duration.seconds(2), ae -> {
			rightHand.setImage(palmRightNormal);
		}));

		leftMainExit = new Timeline(new KeyFrame(Duration.seconds(.5), ae -> {
			leftHand.setImage(leftHandFull);
			confirmComplete.stop();
			confirmComplete.play();
		}), new KeyFrame(Duration.seconds(1), ae -> {
			goToMainMenu();
		}), new KeyFrame(Duration.seconds(2), ae -> {
			leftHand.setImage(palmLeftNormal);
		}));

		changeGalleryR = new Timeline(new KeyFrame(Duration.seconds(.5),
				ae -> {
					rightHand.setImage(rightHandFull);
					quoteView.setImage(quoteButtonUnselected);
					artView.setImage(artButtonHovered);
					confirmComplete.stop();
					confirmComplete.play();
				}), new KeyFrame(Duration.seconds(1), ae -> {
			switchGallery();
		}), new KeyFrame(Duration.seconds(2), ae -> {
			rightHand.setImage(palmRightNormal);
			artView.setImage(artButton);
			quoteView.setImage(quoteButton);
		}));

		changeGalleryL = new Timeline(new KeyFrame(Duration.seconds(.5),
				ae -> {
					leftHand.setImage(leftHandFull);
					quoteView.setImage(quoteButtonUnselected);
					artView.setImage(artButtonHovered);
					confirmComplete.stop();
					confirmComplete.play();
				}), new KeyFrame(Duration.seconds(1), ae -> {
			switchGallery();
		}), new KeyFrame(Duration.seconds(2), ae -> {
			leftHand.setImage(palmLeftNormal);
			artView.setImage(artButton);
			quoteView.setImage(quoteButton);
		}));

		drawHands = new AnimationTimer() {
			@Override
			public void handle(long now) {
				rightHand.setTranslateX(rightHandPosX);
				rightHand.setTranslateY(rightHandPosY);
				leftHand.setTranslateX(leftHandPosX);
				leftHand.setTranslateY(leftHandPosY);

				if (Util.isBetween(517, 787, (int) rightHandPosX)
						&& Util.isBetween(849, 912, (int) rightHandPosY)) {
					changeGalleryR.play();
                    artView.setImage(artButtonHovered);
				} else if (Util.isBetween(517, 787, (int) leftHandPosX)
						&& Util.isBetween(849, 912, (int) leftHandPosY)) {
					changeGalleryL.play();
                    artView.setImage(artButtonHovered);
				} else {
					changeGalleryR.stop();
					changeGalleryL.stop();
					artView.setImage(artButton);
					quoteView.setImage(quoteButton);
				}

				if (Util.isBetween(1353, 1533, (int) rightHandPosX)
						&& Util.isBetween(745, 965, (int) rightHandPosY)) {
					exitView.setImage(exitHoveredImg);
					exitView.setOpacity(1.0);
					rightMainExit.play();
				} else if (Util.isBetween(1353, 1533, (int) leftHandPosX)
						&& Util.isBetween(745, 965, (int) leftHandPosY)) {
					exitView.setImage(exitHoveredImg);
					exitView.setOpacity(1.0);
					leftMainExit.play();
				} else {
					exitView.setImage(exitImg);
					exitView.setOpacity(0.3);
					rightMainExit.stop();
					leftMainExit.stop();
				}
			}
		};

		changeQuotes = new AnimationTimer() {
			@Override
			public void handle(long now) {
				changeQuotes();
			}
		};

		upArrowR = new Timeline(new KeyFrame(Duration.seconds(.5), ae -> {
			rightHand.setImage(rightHandFull);
		}), new KeyFrame(Duration.seconds(1), ae -> {
			direction = true;
			
			changeQuotes.start();
		}), new KeyFrame(Duration.seconds(2), ae -> {
			rightHand.setImage(palmRightNormal);
		}));

		upArrowL = new Timeline(new KeyFrame(Duration.seconds(.5), ae -> {
			leftHand.setImage(leftHandFull);
		}), new KeyFrame(Duration.seconds(1), ae -> {
			direction = true;
			
			changeQuotes.start();
		}), new KeyFrame(Duration.seconds(2), ae -> {
			leftHand.setImage(palmLeftNormal);
		}));

		downArrowR = new Timeline(new KeyFrame(Duration.seconds(.5), ae -> {
			rightHand.setImage(rightHandFull);
		}), new KeyFrame(Duration.seconds(1), ae -> {
			direction = false;
			
			changeQuotes.start();
		}), new KeyFrame(Duration.seconds(2), ae -> {
			rightHand.setImage(palmRightNormal);
		}));

		downArrowL = new Timeline(new KeyFrame(Duration.seconds(.5), ae -> {
			leftHand.setImage(leftHandFull);
		}), new KeyFrame(Duration.seconds(1), ae -> {
			direction = false;

			changeQuotes.start();
		}), new KeyFrame(Duration.seconds(2), ae -> {
			leftHand.setImage(palmLeftNormal);
		}));

		changeArrows = new AnimationTimer() {
			@Override
			public void handle(long now) {
				if (Util.isBetween(700, 700 + 200, (int) rightHandPosX)
						&& Util.isBetween(70, 70 + 80, (int) rightHandPosY)) {
					upArrowR.play();
					upA.setImage(upArrowHovered);
				} else if (Util.isBetween(700, 700 + 200, (int) leftHandPosX)
						&& Util.isBetween(70, 70 + 80, (int) leftHandPosY)) {
					upArrowL.play();
					upA.setImage(upArrowHovered);
				} else if (Util.isBetween(700, 700 + 200, (int) rightHandPosX)
						&& Util.isBetween(650, 650 + 80, (int) rightHandPosY)) {
					downArrowR.play();
					downA.setImage(downArrowHovered);
				} else if (Util.isBetween(700, 700 + 200, (int) leftHandPosX)
						&& Util.isBetween(650, 650 + 80, (int) leftHandPosY)) {
					downArrowL.play();
					downA.setImage(downArrowHovered);
				} else {
					upArrowR.stop();
					upArrowL.stop();
					downArrowR.stop();
					downArrowL.stop();
					upA.setImage(upArrow);
					downA.setImage(downArrow);
                   
				}
                
                 //IF NOT IN ANY BUTTON
                    if(downArrowL.getStatus() ==  Status.STOPPED && downArrowR.getStatus() ==  Status.STOPPED
                      && upArrowL.getStatus() ==  Status.STOPPED && upArrowR.getStatus() ==  Status.STOPPED
                      && !Util.isBetween(517, 787, (int) rightHandPosX)
						&& !Util.isBetween(849, 912, (int) rightHandPosY)&&!Util.isBetween(517, 787, (int) leftHandPosX)
						&& !Util.isBetween(849, 912, (int) leftHandPosY)&&!Util.isBetween(1353, 1533, (int) rightHandPosX)
						&& !Util.isBetween(745, 965, (int) rightHandPosY)&&!Util.isBetween(1353, 1533, (int) leftHandPosX)
						&& !Util.isBetween(745, 965, (int) leftHandPosY)){
                        			leftHand.setImage(palmLeftNormal);
                                			rightHand.setImage(palmRightNormal);

                    }
			}
		};

		canvas.getChildren().addAll(rightHand, leftHand);
		rightHand.relocate(rightHandPosX, rightHandPosY);
		leftHand.relocate(leftHandPosX, leftHandPosY);

		pane.getChildren().add(canvas);
	}

	@Override
	public void setParent(ExperienceController controller) {
		myController = controller;
	}

	@Override
	public void startExperience() {
		drawHands.start();
		sleepTimer.play();
		changeArrows.start();
		controller = new Controller(this);
		QuoteGalleryMusic.playBackgroundMusic();
	}

	@Override
	public void stopExperience() {
		right = null;
		left = null;

		rightHandPosX = -50.0;
		rightHandPosY = -50.0;

		leftHandPosX = -50.0;
		leftHandPosY = -50.0;

		drawHands.stop();
		sleepTimer.stop();
		rightMainExit.stop();
		leftMainExit.stop();
		changeGalleryR.stop();
		changeGalleryL.stop();
		changeQuotes.stop();
		changeArrows.stop();
		QuoteGalleryMusic.pauseBackgroundMusic();
	}

	@Override
	public Node getNode() {
		return pane;
	}

	private void goToMainMenu() {
		stopExperience();
		controller.removeListener(this);
		myController.setExperience(InteractiveWall.MAIN_MENU);
	}

	private void goToArtGallery() {
		controller.removeListener(this);
		myController.setExperience(InteractiveWall.GALLERY);
	}

	public void switchGallery() {
		// stopExperience();
		goToArtGallery();
	}

	public void changeQuotes() {
		if (direction) {
			if (quoteHolder == (quoteList.length - 2)) {
				Text newTop = new Text(quoteList[quoteHolder].getQuote()
						+ "\n- " + quoteList[quoteHolder].getAuthor());
				Text newMain = new Text(quoteList[quoteHolder + 1].getQuote()
						+ "n- " + quoteList[quoteHolder + 1].getAuthor());
				Text newBottom = new Text(quoteList[0].getQuote() + "\n- "
						+ quoteList[0].getAuthor());

				topQ.setText(newTop.getText());
				mainQ.setText(newMain.getText());
				bottomQ.setText(newBottom.getText());

				quoteHolder++;
			} else if (quoteHolder == (quoteList.length - 1)) {
				Text newTop = new Text(quoteList[quoteHolder].getQuote()
						+ "\n- " + quoteList[quoteHolder].getAuthor());
				Text newMain = new Text(quoteList[0].getQuote() + "\n- "
						+ quoteList[0].getAuthor());
				Text newBottom = new Text(quoteList[1].getQuote() + "\n- "
						+ quoteList[1].getAuthor());

				topQ.setText(newTop.getText());
				mainQ.setText(newMain.getText());
				bottomQ.setText(newBottom.getText());

				quoteHolder = 0;
			} else {
				Text newTop = new Text(quoteList[quoteHolder].getQuote()
						+ "\n- " + quoteList[quoteHolder].getAuthor());
				Text newMain = new Text(quoteList[quoteHolder + 1].getQuote()
						+ "\n- " + quoteList[quoteHolder + 1].getAuthor());
				Text newBottom = new Text(quoteList[quoteHolder + 2].getQuote()
						+ "\n- " + quoteList[quoteHolder + 2].getAuthor());

				topQ.setText(newTop.getText());
				mainQ.setText(newMain.getText());
				bottomQ.setText(newBottom.getText());

				quoteHolder++;
			}
		} else {
			if (quoteHolder == 1) {
				Text newTop = new Text(
						quoteList[quoteList.length - 1].getQuote() + "\n- "
								+ quoteList[quoteList.length - 1].getAuthor());
				Text newMain = new Text(quoteList[quoteHolder - 1].getQuote()
						+ "\n- " + quoteList[quoteHolder - 1].getAuthor());
				Text newBottom = new Text(quoteList[quoteHolder].getQuote()
						+ "\n- " + quoteList[quoteHolder].getAuthor());

				topQ.setText(newTop.getText());
				mainQ.setText(newMain.getText());
				bottomQ.setText(newBottom.getText());

				quoteHolder--;
			} else if (quoteHolder == 0) {
				Text newTop = new Text(
						quoteList[quoteList.length - 2].getQuote() + "\n- "
								+ quoteList[quoteList.length - 2].getAuthor());
				Text newMain = new Text(
						quoteList[quoteList.length - 1].getQuote() + "\n- "
								+ quoteList[quoteList.length - 1].getAuthor());
				Text newBottom = new Text(quoteList[quoteHolder].getQuote()
						+ "\n- " + quoteList[quoteHolder].getAuthor());

				topQ.setText(newTop.getText());
				mainQ.setText(newMain.getText());
				bottomQ.setText(newBottom.getText());

				quoteHolder = quoteList.length - 1;
			} else {
				Text newTop = new Text(quoteList[quoteHolder - 2].getQuote()
						+ "\n- " + quoteList[quoteHolder - 2].getAuthor());
				Text newMain = new Text(quoteList[quoteHolder - 1].getQuote()
						+ "\n- " + quoteList[quoteHolder - 1].getAuthor());
				Text newBottom = new Text(quoteList[quoteHolder].getQuote()
						+ "\n- " + quoteList[quoteHolder].getAuthor());

				topQ.setText(newTop.getText());
				mainQ.setText(newMain.getText());
				bottomQ.setText(newBottom.getText());

				quoteHolder--;
			}

		}
		changeQuotes.stop();
	}

	public void onConnect(Controller controller) {
		controller.enableGesture(Gesture.Type.TYPE_SWIPE);
		controller.config().setFloat("Gesture.Swipe.MinLength", 200.0f);
		controller.config().setFloat("Gesture.Swipe.MinVelocity", 1000f);
		controller.config().save();
	}

	public void onFrame(Controller controller) {
		Frame frame = controller.frame();

		HandList hands = frame.hands();

		rightHandPosX = -50.0;
		rightHandPosY = -50.0;
		realRightHandPosX = -50.0;
		realRightHandPosY = -50.0;

		leftHandPosX = -50.0;
		leftHandPosY = -50.0;
		realLeftHandPosX = -50.0;
		realLeftHandPosY = -50.0;

		sleepTimer.play();

		for (int i = 0; i < hands.count(); i++) {
			sleepTimer.stop();

			if (hands.get(i).isRight()) {
				right = hands.get(i);
				rightHandPosX = Util.leapXtoPanelX(right
						.stabilizedPalmPosition().getX());
				rightHandPosY = Util.leapYToPanelY(right
						.stabilizedPalmPosition().getY());
				realRightHandPosX = right.palmPosition().getX();
				realRightHandPosY = right.palmPosition().getY();

			} else if (hands.get(i).isLeft()) {
				left = hands.get(i);
				leftHandPosX = Util.leapXtoPanelX(left.stabilizedPalmPosition()
						.getX());
				leftHandPosY = Util.leapYToPanelY(left.stabilizedPalmPosition()
						.getY());
				realLeftHandPosX = left.palmPosition().getX();
				realLeftHandPosY = left.palmPosition().getY();
			}
		}

		GestureList gestures = frame.gestures();
		SwipeGesture swipe = new SwipeGesture(gestures.get(0));

		Vector swipeStart = swipe.startPosition();
		Vector swipeDir = swipe.direction();

		if (swipe.isValid() && swipeStart.getY() > 130 && swipeDir.getY() < 0) {
			direction = true;
			switch (swipe.state()) {
			case STATE_STOP:
				changeQuotes.start();
				break;
			default:
				break;
			}
		} else if (swipe.isValid() && swipeStart.getY() < -130
				&& swipeDir.getY() > 0) {
			direction = false;
			switch (swipe.state()) {
			case STATE_STOP:
				changeQuotes.start();
				break;
			default:
				break;
			}
		}

	}
}
