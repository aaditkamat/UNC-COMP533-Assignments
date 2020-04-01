package coupledsims.coupler;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import stringProcessors.HalloweenCommandProcessor;
import util.trace.trickOrTreat.LocalCommandObserved;

public class ASimulationCoupler implements PropertyChangeListener {
	HalloweenCommandProcessor observingSimulation;
	
//	public ASimulationCoupler (HalloweenCommandProcessor anObservedSimulaton, HalloweenCommandProcessor anObservingSimulation) {
//		anObservedSimulation.addPropertyChangeListener(this);
//		observingSimulation = anObservingSimulation;
//	}
	public ASimulationCoupler (HalloweenCommandProcessor anObservingSimulation) {
		observingSimulation = anObservingSimulation;
	}
	

	@Override
	public void propertyChange(PropertyChangeEvent anEvent) {
		if (!anEvent.getPropertyName().equals("InputString")) return;
		String newCommand = (String) anEvent.getNewValue();
		LocalCommandObserved.newCase(this, newCommand);
//		System.out.println("Received command:" + newCommand);
		observingSimulation.processCommand(newCommand);
	}

	

}
