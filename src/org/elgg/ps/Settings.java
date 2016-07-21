package org.elgg.ps;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.Nullable;


@State(
 name = "ElggPluginSettings",
 storages = {
	 @Storage(StoragePathMacros.WORKSPACE_FILE)
 })

public class Settings implements PersistentStateComponent<Settings> {
	public boolean pluginEnabled = false;

	public String elggDir = "";
	public String modDir = "";
	public String dataDir = "";
	public String viewsRelativePath = "/views";

	public boolean gotoHandlers = true;
	public boolean gotoViews = true;

	public boolean autocompleteHandlers = true;
	public boolean autocompleteViews = true;

	public boolean dismissEnableNotification = false;

	//@Nullable public List<MethodSignatureSetting> methodSignatureSettings = new ArrayList<MethodSignatureSetting>();

	protected Project project;

	public static Settings getInstance(Project project) {
		Settings settings = ServiceManager.getService(project, Settings.class);
		settings.project = project;

		return settings;
	}

	@Nullable
	@Override
	public Settings getState() {
		return this;
	}

	@Override
	public void loadState(Settings settings) {
		XmlSerializerUtil.copyBean(settings, this);
	}
}

