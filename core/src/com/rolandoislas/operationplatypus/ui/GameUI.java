package com.rolandoislas.operationplatypus.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.scene2d.CompositeActor;

import static com.rolandoislas.operationplatypus.OperationPlatypus.sceneLoader;

/**
 * Created by Rolando on 8/23/2016.
 */
public class GameUI extends Stage {

	private static GameDialogBox dialogBoxSettings;
	private CompositeActor healthIndicator;
	private Table topUi;
	private Table middleUi;
	private Table bottomUi;
	private static CompositeActor dialogBox;
	private static CompositeActor inventory;


	public GameUI() {
		super(new FitViewport(1920 * 3, 1080 * 3));
		Gdx.input.setInputProcessor(this);
		createTable();
		createHealth();
		createDialogBox();
		createInventory();
	}

	private void createInventory() {
		CompositeItemVO vo = sceneLoader.loadVoFromLibrary("uiInventory");
		inventory = new CompositeActor(vo, sceneLoader.getRm());
		inventory.setVisible(false);
		inventory.addScript(new InventoryUi());
		middleUi.add(inventory).padTop(10);
	}

	private void createDialogBox() {
		CompositeItemVO vo = sceneLoader.loadVoFromLibrary("uiDialogBox");
		dialogBox = new CompositeActor(vo, sceneLoader.getRm());
		dialogBox.setVisible(false);
		middleUi.add(dialogBox).padTop(50);
		dialogBox.getItem("buttonConfirm").addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				dialogBoxSettings.confirm(event, x, y);
				dialogBox.setVisible(false);
			}
		});
		dialogBox.getItem("buttonCancel").addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				dialogBoxSettings.cancel(event, x, y);
				dialogBox.setVisible(false);
			}
		});
	}

	private void createTable() {
		Table mainUi = new Table();
		mainUi.setFillParent(true);

		topUi = new Table();
		middleUi = new Table();
		bottomUi = new Table();

		mainUi.add(topUi).expandX();
		mainUi.row();
		mainUi.add(middleUi).expandX();
		mainUi.row();
		mainUi.add(bottomUi).expand();

		addActor(mainUi);
	}

	private void createHealth() {
		CompositeItemVO healthIndicatorVo = sceneLoader.loadVoFromLibrary("uiHealthIndicator");
		healthIndicator = new CompositeActor(healthIndicatorVo, sceneLoader.getRm());
		healthIndicator.addScript(new HealthBar());
		topUi.add(healthIndicator).padTop(10);
	}

	public static void showDialogBox(GameDialogBox dialogBoxSettings) {
		GameUI.dialogBoxSettings = dialogBoxSettings;
		dialogBox.setVisible(true);
		((Label)dialogBox.getItem("message")).setText(dialogBoxSettings.getMessage());
		dialogBox.getItem("buttonConfirm").setVisible(dialogBoxSettings.hasConfirmAction());
		dialogBox.getItem("buttonCancel").setVisible(dialogBoxSettings.hasCancelAction());
	}

	public static CompositeActor getInventory() {
		return inventory;
	}
}
