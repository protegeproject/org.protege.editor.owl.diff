package org.protege.editor.owl.diff.ui;

import org.protege.editor.core.ProtegeManager;
import org.protege.editor.core.editorkit.EditorKitManager;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.diff.model.DifferenceManager;
import org.protege.editor.owl.diff.model.DifferenceEvent;
import org.protege.editor.owl.diff.model.DifferenceListener;
import org.protege.editor.owl.model.selection.OWLSelectionModel;
import org.protege.owl.diff.present.EntityBasedDiff;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class OntologyInAltWorkspaceFactory {
	private OWLEditorKit eKit;
	private OWLEditorKit altEditorKit;
	
	public OntologyInAltWorkspaceFactory(OWLEditorKit eKit) {
		this.eKit = eKit;
	}
	
	public OWLOntology loadInSeparateSynchronizedWorkspace(IRI ontologyLocation) throws OWLOntologyCreationException {
		try {
			altEditorKit = (OWLEditorKit) (eKit.getEditorKitFactory()).createEditorKit();
		}
		catch (Exception e) {
			throw new OWLOntologyCreationException("Could not create editor kit", e);
		}
		OWLOntology ontology = null;
		try {
			ontology = loadOntology(altEditorKit.getOWLModelManager().getOWLOntologyManager(), ontologyLocation);
		}
		finally {
			if (ontology == null) {
				altEditorKit.dispose();
			}
		}
		altEditorKit.getOWLModelManager().setActiveOntology(ontology);
		ProtegeManager.getInstance().getEditorKitManager().addEditorKit(altEditorKit);
		altEditorKit.getOWLWorkspace().setTitle("Workspace for original version of ontology");
		eKit.getOWLWorkspace().requestFocusInWindow();
		return ontology;
	}
	
	/**
	 * Loads the ontology using the ontology manager.
	 * 
	 * This method is trivial.  The purpose of introducing this method is to allow the caller to make any 
	 * needed fixups to the ontology before the ontology is added to the separate workspace.
	 * 
	 * @param manager
	 * @param ontologyLocation
	 * @return
	 * @throws OWLOntologyCreationException
	 */
	protected OWLOntology loadOntology(OWLOntologyManager manager, IRI ontologyLocation) throws OWLOntologyCreationException {
		return manager.loadOntologyFromOntologyDocument(ontologyLocation);
	}
	
	public OWLEditorKit getAltEditorKit() {
		return altEditorKit;
	}
}