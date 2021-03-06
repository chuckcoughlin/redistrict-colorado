/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.plan;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import redistrict.colorado.core.AnalysisModel;
import redistrict.colorado.core.PlanFeature;
import redistrict.colorado.core.PlanModel;
import redistrict.colorado.gate.AggregateTask;
import redistrict.colorado.ui.ComponentIds;

/**
 * Display a progress dialog while aggregating features for a plan.
 * @author chuckc
 *
 */
public class FeatureProgressDialog extends Dialog<List<PlanFeature>> {
	private final static String CLSS = "FeatureProgressDialog";
	private final static Logger LOGGER = Logger.getLogger(CLSS);
	private final double LABEL_WIDTH = 250.;
	private final double PROGRESS_WIDTH = 200.;
	
	private final AggregateTask task;
	private final FlowPane root;
	private final Button cancelButton;
	private final Label statusLabel;
	private final ProgressBar progressBar; 
	
	public FeatureProgressDialog(PlanModel model,AnalysisModel am) {
		this.task = new AggregateTask(model,am);
		
		this.setTitle("Aggregating Feature Information");
		this.setHeaderText("Aggregate affiliation and demographic information into plan boundaries.");
		this.progressBar = new ProgressBar(0);
		progressBar.setProgress(0.);
		progressBar.setPrefWidth(PROGRESS_WIDTH);
		this.cancelButton= new Button("Cancel");
		cancelButton.setDisable(false);
		cancelButton.setId(ComponentIds.BUTTON_CANCEL);
		cancelButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				cancelButton.setDisable(true);
				task.cancel(true);
				progressBar.progressProperty().unbind();
				statusLabel.textProperty().unbind();
				progressBar.setProgress(0);
				setResult(new ArrayList<PlanFeature>());
				close();
			}
		});
		this.statusLabel = new Label();
		statusLabel.setMinWidth(LABEL_WIDTH);
	    statusLabel.setTextFill(Color.BLUE);
	    
	    // Bind task to progress bar
	    progressBar.progressProperty().unbind();
	    progressBar.progressProperty().bind(task.progressProperty());
	    statusLabel.textProperty().unbind();
	    statusLabel.textProperty().bind(task.messageProperty());
	    
	    // Auto close on completion
	    task.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED,
	    						new EventHandler<WorkerStateEvent>() {
	    	@Override
	    	public void handle(WorkerStateEvent t) {;
	    		statusLabel.textProperty().unbind();
	    		statusLabel.setText("Complete");
	    		LOGGER.info(String.format("%s: Exiting ...,", CLSS));
	    		try {
	    			setResult(task.get());
	    		}
	    		catch(InterruptedException ie) {
	    			LOGGER.info(String.format("%s: Interrupted getting results (%s)", CLSS, ie.getMessage()));
	    		}
	    		catch(ExecutionException ee) {
	    			LOGGER.info(String.format("%s: Error results (%s)", CLSS, ee.getMessage()));
	    		}
	    		close();
	    	}
	    });
	    
	    
	    this.root = new FlowPane();
	    FlowPane root = new FlowPane();
	    root.setPadding(new Insets(10));
	    root.setHgap(10);
	    root.setPrefWrapLength(500);
	    root.setColumnHalignment(HPos.RIGHT); 
	    root.getChildren().addAll(statusLabel,progressBar,cancelButton);
        getDialogPane().setContent(root);
        
	    // Start the Task.
	    new Thread(task).start();
	}
}
