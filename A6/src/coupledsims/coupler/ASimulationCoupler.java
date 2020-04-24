package coupledsims.coupler;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;

import stringProcessors.HalloweenCommandProcessor;
import util.trace.trickOrTreat.LocalCommandObserved;

public class ASimulationCoupler implements PropertyChangeListener, Serializable {
	HalloweenCommandProcessor observingSimulation;

	public ASimulationCoupler (HalloweenCommandProcessor anObservingSimulation) {
		observingSimulation = anObservingSimulation;
	}
	

	@Override
	public void propertyChange(PropertyChangeEvent anEvent) {
		if (!anEvent.getPropertyName().equals("InputString")) return;
		String newCommand = (String) anEvent.getNewValue();
		LocalCommandObserved.newCase(this, newCommand);
		observingSimulation.processCommand(newCommand);
	}

	

}
