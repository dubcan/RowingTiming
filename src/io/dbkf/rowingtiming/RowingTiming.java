package io.dbkf.rowingtiming;

import java.util.Arrays;
import java.util.Collection;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class RowingTiming extends Application {

	private final Collection<MillisToStyle> millisToStyles = Arrays.asList(
			new MillisToStyle(1000, "-fx-background: #ffa500;"), new MillisToStyle(2000, "-fx-background: #777777;"));
	private String currentStyle;

	private static class MillisToStyle {
		private final long millis;
		private final String style;

		public MillisToStyle(long millis, String style) {
			this.millis = millis;
			this.style = style;
		}

		public long getMillis() {
			return millis;
		}

		public String getStyle() {
			return style;
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		stage.setTitle("RowingTiming");
		stage.setFullScreen(true);
		stage.centerOnScreen();

		StackPane root = new StackPane();
		root.setStyle(millisToStyles.iterator().next().getStyle());

		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();

		long maxMillis = getMaxMillis(millisToStyles);

		long startTime = System.currentTimeMillis();
		Label label = new Label(formatSeconds(getDiff(startTime) % maxMillis));

		Font font = new Font("Ubuntu Condensed", 350);
		label.setFont(font);
		label.setStyle("-fx-background: #000000;");
		root.getChildren().add(label);
		
		AnimationTimer timer = new AnimationTimer() {
			@Override
			public void handle(long now) {
				long diff = getDiff(startTime);
				long cuttedDiff = diff % maxMillis;
				label.setText(formatSeconds(cuttedDiff));
				String style = getStyle(cuttedDiff, millisToStyles);
				if (!style.equals(currentStyle))
					currentStyle = style;

				root.setStyle(currentStyle);
			}
		};
		timer.start();
	}

	private String getStyle(long diff, Collection<MillisToStyle> millisToStyles) {
		long accum = 0;
		for (MillisToStyle millisToStyle : millisToStyles) {
			accum += millisToStyle.getMillis();
			if (diff <= accum)
				return millisToStyle.getStyle();
		}
		throw new IllegalArgumentException(
				"Can't get style for diff = " + diff + ". millisToStyles = " + millisToStyles);
	}

	// 2575 to "2.5"
	private String formatSeconds(long diff) {
		long ss = diff / 1000;
		long ms = diff % 1000 / 100;
		String result = ss + "." + ms;
		return result;
	}

	private long getMaxMillis(Collection<MillisToStyle> msToStyles) {
		long result = 0;
		for (MillisToStyle millisToStyle : msToStyles) {
			result += millisToStyle.getMillis();
		}
		return result;
	}

	private long getDiff(long startTime) {
		return System.currentTimeMillis() - startTime;
	}
}
