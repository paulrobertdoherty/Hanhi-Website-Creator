//Implement the code editor
var editor = ace.edit("code");
editor.setTheme("ace/theme/monokai");
editor.getSession().setMode("ace/mode/javascript");
editor.$blockScrolling = Infinity;

//Set up code changing
var current = "html";
var currentSave = {
		content: {
			debug:
				"//This is a sample program that repeats a string n number of times.\n" +
				"//Keep in mind that when creating and setting variables, use the\n" +
				"//v function, which records the value of variables.\n" +
				"//For example, when typing \"var i = 0;\", you would actually type\n" +
				"//\"var i = v(\"i\", 0);\".  You would also type \"i = v(\"i\", 1);\"\n" +
				"//for \"i = 1;\".  Press the \"Run\" button to run the program and\n" +
				"//generate snapshops, and then press the \"Cycle\" button to cycle\n" +
				"//through each snapshot.  The \"Cycle Back\" button cycles in reverse,\n" +
				"//and adding \"true\" as the last parameter of a v function stops it\n" +
				"//from recording.  For example, \"var i = v(\"i\", 0, true);\" is the same\n" +
				"//as \"var i = 0;\", while \"var i = v(\"i\", 0);\" is not \"var i = 0;\".\n" +
				"function repeat(str, n) {\n" +
				"	var toReturn = v(\"toReturn\", \"\");\n" +
				"	for (var i = v(\"i\", 0); i < n; i = v(\"i\", i + 1)) {\n" +
				"		toReturn = v(\"toReturn\", toReturn + str);\n" +
				"	}\n" +
				"	 return toReturn;\n" +
				"}\n" +
				"repeat(\"A\", 5);",
			javascript:
				"$(\"h1\").click(function() {\n" +
				"	alert(\"Hello world!\");\n" +
				"});",
			html: "<h1>Hello world!</h1>",
			htmlHead: "<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/2.2.4/jquery.min.js\"></script>",
			css: "h1 {\n  font-size: 32px;\n}"
		},
		tags: ""
};

$("#html").click(function() {
	saveCode();
	current = "html";
	editor.getSession().setMode("ace/mode/html");
	editor.setValue(currentSave.content.html);
	$("#label").text("HTML Body");
});
$("#htmlHead").click(function() {
	saveCode();
	current = "htmlHead";
	editor.getSession().setMode("ace/mode/html");
	editor.setValue(currentSave.content.htmlHead);
	$("#label").text("HTML Header");
});
$("#css").click(function() {
	saveCode();
	current = "css";
	editor.getSession().setMode("ace/mode/css");
	editor.setValue(currentSave.content.css);
	$("#label").text("CSS");
});
$("#javascript").click(function() {
	saveCode();
	current = "javascript";
	editor.getSession().setMode("ace/mode/javascript");
	editor.setValue(currentSave.content.javascript);
	$("#label").text("Javascript");
});
$("#debug").click(function() {
	saveCode();
	current = "debug";
	editor.getSession().setMode("ace/mode/javascript");
	editor.setValue(currentSave.content.debug);
	$("#label").text("Javascript Debugging");
});
//Saves the code 
function saveCode() {
	switch (current) {
		case "debug":
			currentSave.content.debug = editor.getValue();
			break;
		case "javascript":
			currentSave.content.javascript = editor.getValue();
			break;
		case "html":
			currentSave.content.html = editor.getValue();
			break;
		case "htmlHead":
			currentSave.content.htmlHead = editor.getValue();
			break;
		case "css":
			currentSave.content.css = editor.getValue();
			break;
	}
}
editor.getSession().on('change', saveCode);

//For when the user wants to code
$("#enterOwn").click(function() {
	$("#choose").css("display", "none");
	$("#switch").css("display", "inline");
	$("#code").css("display", "block");
	usingTemplates = false;
	
});

//Handle templates
var usingTemplates = false;
var templates = [];
var defaultTemplate = null;

var index = 0;

function addTemplateParam(tag) {
	var label = $(document.createElement("label"));
	label.text(tag.header);
	var params = $("#templateParamsLoc");
	params.append(label);
	$("#templateParamsLoc").append("<input value=\"" + tag.value + "\" id=\"" + index + "\" type=\"text\" class=\"form-control\">");
	index++;
}

function openTemplate(template) {
	usingTemplates = true;
	defaultTemplate = {
			content: template.content,
			tags: template.tags
	};
	$("#templateLoc").empty();
	$("#chooseTemplateArea").css("display", "none");
	var tags = template.tags;
	$("#templateParamsLoc").empty();
	index = 0;
	$("#templateParams").css("display", "block");
	for (var i = 0; i < tags.length; i++) {
		addTemplateParam(tags[i]);
	}
}

function getTags() {
	if (usingTemplates) {
		var newTags = defaultTemplate.tags;
		for (var i = 0; i < newTags.length; i++) {
			newTags[i].value = $("#" + i).val();
		}
		var json = "";
		for (var i = 0; i < newTags.length; i++) {
			json += JSON.stringify(newTags[i]) + "JSON_SPLITTER_STRING";
		}
		return json;
	} else {
		return "NO_TAGS";
	}
}

function addTemplate(template) {
	templates.push(template);
	$("#templateLoc").append("<button type=\"button\" class=\"btn btn-block btn-default\" onclick=\"openTemplate(templates[" +
			(templates.length - 1) + "])\"><img src=" + template.thumbnail + "><br><label>" + template.title + "</label></button>");
}

function getTemplateFromXML(result) {
	var xml = $(result);
	var tags = [];
	var tagObjs = xml.find("tag").toArray();
	for (var i = 0; i < tagObjs.length; i++) {
		var tag = $(tagObjs[i]);
		tags.push({
			name: tag.find("name").text(),
			header: tag.find("header").text(),
			value: tag.find("value").text()
		});
	}
	return {
		title: xml.find("title").text(),
		thumbnail: xml.find("thumbnail").text(),
		content: {
			debug: "",
			javascript: xml.find("javascript").text(),
			html: xml.find("htmlBody").text(),
			htmlHead: xml.find("htmlHead").text(),
			css: xml.find("css").text()
		},
		tags: tags
	};
}

function getTemplateFromHTML(url, result) {
	var tags = [];
	tags.push({
		name: "Senator",
		header: "Replace \"Senator\" with...",
		value: "Elf-lord"
	});
	return {
		title: url,
		thumbnail: "favicon.ico",
		content: {
			debug: "",
			javascript: "NONE",
			html: result.replace("</head>", "<base href=\"" + url + "\"></head>"),
			htmlHead: "NONE",
			css: "NONE"
		},
		tags: tags
	};
}

$("#replace").click(function() {
	var toReplace = $("#replaceThis").val();
	var tag = {
			name: toReplace,
			header: "Replace \"" + toReplace + "\" with...",
			value: ""
		};
	addTemplateParam(tag);
	defaultTemplate.tags.push(tag);
});

function getTemplate(url) {
	$.ajax({
		url: url.endsWith(".xml") ? url : "/mirror?url=" + url,
		success:
			function(data, textStatus, request) {
				try {
					var header = request.getResponseHeader("content-type");
					if (header.indexOf("text/html") != -1) {
						addTemplate(getTemplateFromHTML(url, data));
					} else if (header.indexOf("application/xml") != -1) {
						addTemplate(getTemplateFromXML(data));
					} else {
						alert("This is not a valid template!  It has the header of " + header + "!");
					}
				} catch (e) {
					alert("This is not a valid template!" + e);
				}
			},
		error:
			function(jqXHR, status, error) {
				alert("Something went wrong with loading the template.  Otherwise known as:" + status + ", " + error);
			}
	});
}
$("#chooseTemplate").click(function() {
	$("#choose").css("display", "none");
	$("#template").css("display", "inline");
	$("#chooseTemplateArea").css("display", "inline");
	
	//Add the templates
	usingTemplates = true;
	var c = 3;
	for (var i = 0; i < c; i++) {
		getTemplate("templates/" + i + ".xml");
	}
});
$("#templateURLButton").click(function() {
	getTemplate($("#templateURL").val());
	$("#templateURL").val("");
});

//Handle projects
var url = "n";
var urlMap = [];

//All the names and values of the variables
var vars = [];

//The history of the program
var varSlices = [];

function deepCopy(array) {
	var newest = [];
	for (var i = 0; i < array.length; i++) {
		newest.push(array[i]);
	}
	return newest;
}

//Add the variable table to the slice
function slow(name){
	varSlices.push({slice: deepCopy(vars), active: name});
}

//Adds a variable
function v(name, value, add) {
	add = add || 0;
	if (add == 0) {
		var changed = false;
		var toAdd;
		var type = typeof(value);
		if (type == null || type == "object") {
			if (value.constructor === Array) {
				toAdd = {name, value: jQuery.extend(true, [], value)};
			} else {
				toAdd = {name, value: jQuery.extend(true, {}, value)};
			}
		} else {
			toAdd = {name: name, value: value};
		}
		for (var i = 0; i < vars.length; i++) {
			if (vars[i].name == name) {
				vars[i] = toAdd;
				changed = true;
				break;
			}
		}
		if (!changed) {
			vars.push(toAdd);
		}
		slow(name);
	}
	return value;
}

function changeTable(slice) {
	$("#body").empty();
	if (slice != null) {
		var active = slice.active;
		var cSlice = slice.slice;
		for (var i = 0; i < cSlice.length; i++) {
			if (cSlice[i].name == active) {
				$("#body").append("<tr class='success'><td>" + cSlice[i].name + "</td><td>" + cSlice[i].value + "</td></tr>");
			} else {
				$("#body").append("<tr><td>" + cSlice[i].name + "</td><td>" + cSlice[i].value + "</td></tr>");
			}
		}
	}
}

var i = 0;

function goThroughSlice() {
	changeTable(varSlices[i]);
	if (i < varSlices.length - 1) {
		i++;
	}
}

function goBack() {
	changeTable(varSlices[i]);
	if (i > 0) {
		i--;
	}
}

$("#update").click(goThroughSlice);
$("#back").click(goBack);

function save(func) {
	$("#savedMessage").text("...");
	var tags = getTags();
	$.post("/save", {id: id, debug: currentSave.content.debug, javascript: currentSave.content.javascript, 
		html: currentSave.content.html, htmlHead: currentSave.content.htmlHead, css: currentSave.content.css,
		url:url, tags: tags}, function(data) {
		if (!data.startsWith("ERROR: ")) {
			//Add the save and share buttons
			$("#savedMessage").html("Saved! <a class=\"btn tweet\"href=\protect?url=" + data + ">Tweet</a>");
			if (urlMap[data] == null) {
				urlMap[data] = {
						content: currentSave.content,
						tags: tags
				};
				addSave(data);
				op(data);
			}
			func("/v?i=" + data);
		} else {
			console.log(data);
			$("#savedMessage").text("");
		}
	});
}

$("#run").click(function() {
	if (!usingTemplates) {
		saveCode();
	} else {
		currentSave.content = {
				debug: "",
				html: defaultTemplate.content.html,
				htmlHead: defaultTemplate.content.htmlHead,
				css: defaultTemplate.content.css,
				javascript: defaultTemplate.content.javascript
		}
	}
	if (defaultTemplate != null || !usingTemplates) {
		save(function(path) {
			//Open the url
			document.getElementById("frame").src = path;
			if (currentSave.debug !== undefined && currentSave.debug.length > 0) {
				//Do the Javascript debugging
				$(".debugTable").css("display", "inline");
				vars = [];
				varSlices = [];
				i = 0;
				eval(currentSave.debug);
				goThroughSlice();
			}
		});
	}
});

var id = "NONE";

//Sign in stuff
function onSignIn(googleUser) {
    //Validate the token
    var idTok = googleUser.getAuthResponse().id_token;
    $.post("/sign", {idToken: idTok}, function(data) {
    	var correct = "Signed in successfully!";
    	if (data == correct) {
    		//Change sign in to sign out
            $("#sign").text("Sign Out");
            $("#sign").click(onSignOut);
            id = googleUser.getBasicProfile().getId();
            changeContents();
            getSaved();
            $("#openDropdown").css("display", "inline");
    	}
    });
}

function updateCode() {
	switch (current) {
		case "debug":
			editor.setValue(currentSave.content.debug);
			break;
		case "javascript":
			editor.setValue(currentSave.content.javascript);
			break;
		case "html":
			editor.setValue(currentSave.content.html);
			break;
		case "htmlHead":
			editor.setValue(currentSave.content.htmlHead);
			break;
		case "css":
			editor.setValue(currentSave.content.css);
			break;
	}
}

function update() {
	if (urlMap[url] != undefined) {
		currentSave = urlMap[url];
		
		if (url == "n") {
			//Change the view
			$("#choose").css("display", "block");
			$("#switch").css("display", "none");
			$("#code").css("display", "none");
			$("#templateParams").empty();
			$("#template").css("display", "inline");
			$("#chooseTemplateArea").css("display", "none");
		} else if (urlMap[url].tags != null && urlMap[url].tags.length > 0 && urlMap[url].tags != "NO_TAGS") {
			//Change the view
			$("#choose").css("display", "none");
			$("#switch").css("display", "none");
			$("#code").css("display", "none");
			$("#template").css("display", "inline");
			$("#chooseTemplateArea").css("display", "none");
			
			var tags = urlMap[url].tags;
			if (typeof tags === "string") {
				var tagList = tags.split("JSON_SPLITTER_STRING");
				var object = [];
				for (var i = 0; i < tagList.length; i++) {
					var c = tagList[i];
					if (c.length > 0)
					object.push(JSON.parse(tagList[i]));
				}
				tags = object;
			}
			
			openTemplate({
				content: urlMap[url].content,
				tags: tags
			});
		} else {
			//Change the view
			$("#template").css("display", "none");
			$("#choose").css("display", "none");
			$("#code").css("display", "block");
			$("#switch").css("display", "inline");
			
			updateCode();
		}
	} else {
		updateCode();
	}
}

//open
function op(txt) {
	urlMap[url] = $.extend(true, {}, currentSave);
	url = txt;
	
	if (txt == "n") {
		$("#urlSpace").text("New Site");
	} else {
		$("#urlSpace").text(txt);
	}
	update();
}

function getSaved() {
	$.get("/getSaved?id=" + id, function(data) {
		if (!data.startsWith("ERROR: ") && data != undefined) {
			var saves = data.split("} END URL;");
			for (var i = 0; i < saves.length; i++) {
				var save = saves[i];
				if (save != null && save != undefined && save.length > 0) {
					var toAdd = {
							debug: save.split("DEBUG {")[1].split("} JAVASCRIPT {")[0],
							javascript: save.split("} JAVASCRIPT {")[1].split("} HTML {")[0],
							html: save.split("} HTML {")[1].split("} HTML_HEAD {")[0],
							htmlHead: save.split("} HTML_HEAD {")[1].split("} CSS {")[0],
							css: save.split("} CSS {")[1].split("} URL: {")[0]
					}
					var u = "";
					var tags = "";
					if (save.indexOf("} TAGS {") != -1) {
						u = save.split("} URL: {")[1].split("} TAGS {")[0];
						tags = save.split("} TAGS {")[1];
					} else {
						u = save.split("} URL: {")[1];
					}
					if (urlMap[u] === undefined) {
						addSave(u);
					}
					urlMap[u] = {
							content: toAdd,
							tags: tags
					};
				}
			}
		} else {
			console.log(data);
		}
	});
}

function addSave(urlOf) {
	$("#openList").append("<li onclick=\"op('" + urlOf + "');\"><a>" + urlOf + "</a></li>");
}

function changeContents() {
    $("#editor").css("display", "inline");
    $(".content").css("display", "none");
    update();
}

$("#save").click(save);
$("#openEditor").click(changeContents);

function onSignOut() {
    gapi.auth2.getAuthInstance().signOut();
    location.reload();
}