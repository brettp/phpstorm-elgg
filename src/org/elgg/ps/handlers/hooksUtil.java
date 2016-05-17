/*
 * Copyright (c) 2016 Brett Profitt.
 *
 * This file is part of the Elgg support plugin for JetBrains PhpStorm IDE.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.elgg.ps.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class hooksUtil {
	public static Map<String, List<Integer>> handlerFuncs = new HashMap<String, List<Integer>>() {{
		put("elgg_register_plugin_hook_handler", new ArrayList<Integer>() {{
			add(0);
		}});
		put("\\Elgg\\PluginHooksService::register", new ArrayList<Integer>() {{
			add(0);
		}});
		put("elgg_unregister_plugin_hook_handler", new ArrayList<Integer>() {{
			add(0);
		}});
		put("\\Elgg\\PluginHooksService::unregister", new ArrayList<Integer>() {{
			add(0);
		}});
		put("elgg_trigger_plugin_hook", new ArrayList<Integer>() {{
			add(0);
		}});
		put("\\Elgg\\PluginHooksService::trigger", new ArrayList<Integer>() {{
			add(0);
		}});
	}};
}
