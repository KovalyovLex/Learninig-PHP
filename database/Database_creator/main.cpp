#include <fstream>
#include <string>
#include <sstream>
#include <iostream>
#include <vector>
#include <cstdlib>
#include <sys/types.h>
#include <dirent.h>

using namespace std;

string name_beg = "<h1 class=\"refname\">", name_fin = "</h1>";
string ver_beg = "<p class=\"verinfo\">", ver_fin = "</p>";
string sh_desc_beg = "<span class=\"dc-title\">", sh_desc_fin = "</span>";
string desc_beg = "<div class=\"methodsynopsis dc-description\">", desc_fin = "</div>"; // finish on 2-nd div, on 1-st div " - "
string params_beg = "<span class=\"term\">", params_fin = "</div>"; // <p class="para"> замена " - "
string return_val_beg = "<div class=\"refsect1 returnvalues\"", return_val_fin = "</div>"; // начало с <p class="para">
string errors_beg = "<div class=\"refsect1 errors\"", errors_fin = "</p>"; // start <p class="para">
string examples_beg = "<div class=\"refsect1 examples\"", examples_fin = "</div>"; // начало с <div class="example-contents">
string examples_res_beg = "<div class=\"cdata\">", examples_res_fin = "</div>";


string normalsign(string Text){
	for(unsigned index=0; index=Text.find("&quot;", index), index!=std::string::npos;)
	{
		Text.replace(index, 6, "\"");
	}
	for(unsigned index=0; index=Text.find("&nbsp;", index), index!=std::string::npos;)
	{
		Text.replace(index, 6, " ");
	}
	for(unsigned index=0; index=Text.find("&amp;", index), index!=std::string::npos;)
	{
		Text.replace(index, 5, "&");
	}
	for(unsigned index=0; index=Text.find("&lt;", index), index!=std::string::npos;)
	{
		Text.replace(index, 4, "<");
	}
	for(unsigned index=0; index=Text.find("&gt;", index), index!=std::string::npos;)
	{
		Text.replace(index, 4, ">");
	}
	for(unsigned index=0; index=Text.find("&and;", index), index!=std::string::npos;)
	{
		Text.replace(index, 5, "∧");
	}
	for(unsigned index=0; index=Text.find("&reg;", index), index!=std::string::npos;)
	{
		Text.replace(index, 5, "®");
	}
	for(unsigned index=0; index=Text.find("&para;", index), index!=std::string::npos;)
	{
		Text.replace(index, 6, "¶");
	}
	for(unsigned index=0; index=Text.find("&trade;", index), index!=std::string::npos;)
	{
		Text.replace(index, 7, "™");
	}
	for(unsigned index=0; index=Text.find("&lsaquo;", index), index!=std::string::npos;)
	{
		Text.replace(index, 8, "‹");
	}
	for(unsigned index=0; index=Text.find("&rsaquo;", index), index!=std::string::npos;)
	{
		Text.replace(index, 8, "›");
	}
	for(unsigned index=0; index=Text.find("&euro;", index), index!=std::string::npos;)
	{
		Text.replace(index, 6, "€");
	}
	for(unsigned index=0; index=Text.find("&bdquo;", index), index!=std::string::npos;)
	{
		Text.replace(index, 7, "„");
	}
	for(unsigned index=0; index=Text.find("&rdquo;", index), index!=std::string::npos;)
	{
		Text.replace(index, 7, "”");
	}
	for(unsigned index=0; index=Text.find("&ldquo;", index), index!=std::string::npos;)
	{
		Text.replace(index, 7, "“");
	}
	for(unsigned index=0; index=Text.find("&sbquo;", index), index!=std::string::npos;)
	{
		Text.replace(index, 7, "‚");
	}
	for(unsigned index=0; index=Text.find("&rsquo;", index), index!=std::string::npos;)
	{
		Text.replace(index, 7, "’");
	}
	for(unsigned index=0; index=Text.find("&lsquo;", index), index!=std::string::npos;)
	{
		Text.replace(index, 7, "�?");
	}
	for(unsigned index=0; index=Text.find("&mdash;", index), index!=std::string::npos;)
	{
		Text.replace(index, 7, "—");
	}
	for(unsigned index=0; index=Text.find("&ndash;", index), index!=std::string::npos;)
	{
		Text.replace(index, 7, "–");
	}
	for(unsigned index=0; index=Text.find("&tilde;", index), index!=std::string::npos;)
	{
		Text.replace(index, 7, "˜");
	}
	for(unsigned index=0; index=Text.find("&circ;", index), index!=std::string::npos;)
	{
		Text.replace(index, 6, "ˆ");
	}
	for(unsigned index=0; index=Text.find("&dagger;", index), index!=std::string::npos;)
	{
		Text.replace(index, 8, "†");
	}
	for(unsigned index=0; index=Text.find("&Dagger;", index), index!=std::string::npos;)
	{
		Text.replace(index, 8, "‡");
	}
	return Text;
}

string delspaces(string Text, vector<unsigned> prefs_beg, vector<unsigned> prefs_fin){
	bool isPre = false;	
	for(unsigned index=0; index=Text.find("  ", index), index!=std::string::npos;)
	{
		isPre = false;
		for (int i=0; i < prefs_beg.size();i++)
			isPre = (isPre) || ( (index >= prefs_beg[i]) && (index <= prefs_fin[i]) );
		if (!isPre)
			Text.replace(index, 2, " ");
		else
			index++;
	}
	if (prefs_beg.size() > 0){
		if (prefs_beg[0] > 0){
			if (Text[0] == ' '){
				string res = "";
				res.append(Text,1,Text.length()-1);
				return normalsign(res);
			}
		}
	}else
	if (Text[0] == ' '){
		string res = "";
		res.append(Text,1,Text.length()-1);
		return normalsign(res);
	}
	return normalsign(Text);
}

string delspaces(string Text){
	for(unsigned index=0; index=Text.find("  ", index), index!=std::string::npos;)
	{
		Text.replace(index, 2, " ");
	}
	if (Text[0] == ' '){
		string res = "";
		res.append(Text,1,Text.length()-1);
		return normalsign(res);
	}
	return normalsign(Text);
}

string getText(string Text){
	vector<unsigned> prefs_beg = vector<unsigned>(), prefs_fin = vector<unsigned>();
	for(unsigned index=0; index=Text.find("<pre>", index), index!=std::string::npos;)
	{
		index += 5;
		prefs_beg.push_back(index);
		prefs_fin.push_back(Text.find("</pre>", index));
	}
	bool isPre = false;
	for(unsigned index=0; index=Text.find("\n", index), index!=std::string::npos;)
	{
		isPre = false;
		for (int i=0; i < prefs_beg.size();i++)
			isPre = (isPre) || ( (index >= prefs_beg[i]) && (index <= prefs_fin[i]) );
		if (!isPre)
			Text.replace(index, 1, "");
		else
			index++;
	}
	for(unsigned index=0; index=Text.find("<p", index), index!=std::string::npos;)
	{
		Text.replace(index, 4, "\n <p");
		index+=4;
	}
	for(unsigned index=0; index=Text.find("<br />", index), index!=std::string::npos;)
	{
		Text.replace(index, 6, "\n");
		index+=4;
	}
	int first = 0, last = 0, beg = 0;
	string res = "";
	first = Text.find("<",last);
	last = Text.find(">",first) + 1;
	if (first == std::string::npos) return delspaces(Text);
	while (first != std::string::npos){
		res.append(Text, beg, (first - beg));
		beg = last;
		first = Text.find("<",last);
		if (first == std::string::npos) break;
		last = Text.find(">",first) + 1;
	}
	res.append(Text,last,Text.length()-last);
	return delspaces(res,prefs_beg,prefs_fin);
}


string getName(string str){
	string name = "";
	int begin = str.find(name_beg,0), fin = str.find(name_fin,begin);
	if (begin == std::string::npos) return name;
	begin += name_beg.length();
	name.append(str, begin, (fin-begin));
	return getText(name);
}

string getVer(string str){
	string ver = "";
	int begin = str.find(ver_beg,0), fin = str.find(ver_fin,0);
	if (begin == std::string::npos) return ver;
	begin += ver_beg.length();
	ver.append(str, begin, (fin-begin));
	return getText(ver);
}

string getSh_desc(string str){
	string sh_desc = "";
	int begin = str.find(sh_desc_beg,0), fin = str.find(sh_desc_fin,begin);
	if (begin == std::string::npos) return sh_desc;
	begin += sh_desc_beg.length();
	sh_desc.append(str, begin, (fin-begin));
	return getText(sh_desc);
}

string getDesc(string str){
	string desc = "";
	int begin = str.find(desc_beg,0), fin = str.find(desc_fin,begin);
	if (begin == std::string::npos) return desc;
	begin += desc_beg.length();
	fin = str.find(desc_fin,fin+4);
	desc.append(str, begin, (fin-begin));
	return getText(desc);
}

string getParams(string str){
	string params = "", first = "";
	int begin = str.find(params_beg,0), end;
	if (begin == std::string::npos) return params;
	begin += params_beg.length();
	end = str.find(params_fin,begin);
	int next = str.find("<p class=\"para\">",begin), fin;
	while((next < end)&&(next != std::string::npos)){
		first = "";
		first.append(str,begin,(next-begin));
		first = getText(first);
		params += first + " - ";
		next += string("<p class=\"para\">").length();
		fin = str.find("</p>",next);
		first = "";
		first.append(str,next,(fin-next));
		first = getText(first);
		params += first + "\n";
		begin = str.find(params_beg,fin);
		if (begin == std::string::npos) break;
		begin += params_beg.length();
		next = str.find("<p class=\"para\">",begin);
		if (next == std::string::npos) break;
	}
	first = delspaces(params);
	params = "";
	params.append(first,0,first.length()-1);
	return params;
}

string getRet_val(string str){
	string res = "";
	int begin = str.find(return_val_beg,0), 
		end = str.find(return_val_fin,begin);
	if (begin == std::string::npos) return res;
	begin += return_val_beg.length();
	begin = str.find("<p class=\"para\">",begin);
	if (begin == std::string::npos) return res;
	begin += string("<p class=\"para\">").length();	
	end = str.find("</p>",begin);
	res.append(str,begin,(end-begin));
	return getText(res);
}

string getExamples(string str){
	string res = "";
	int begin = str.find(examples_beg,0), 
		end = str.find(examples_fin,begin);
	if (begin == std::string::npos) return res;
	begin += examples_beg.length();
	begin = str.find("<div class=\"example-contents\">",begin);
	if (begin == std::string::npos) return res;
	begin += string("<div class=\"example-contents\">").length();
	res.append(str,begin,(end-begin));
	return getText(res);
}

string getExamples_Res(string str){
	string res = "";
	int begin = str.find(examples_res_beg,0), 
		end = str.find(examples_res_fin,begin);
	if (begin == std::string::npos) return res;
	begin += examples_res_beg.length();
	res.append(str,begin,(end-begin));
	return getText(res);
}

string getErrors(string str){
	string res = "";
	int begin = str.find(errors_beg,0), 
		end = str.find(errors_fin,begin);
	if (begin == std::string::npos) return res;
	begin += errors_beg.length();
	begin = str.find("<p class=\"para\">",begin);
	if (begin == std::string::npos) return res;
	begin += string("<p class=\"para\">").length();
	res.append(str,begin,(end-begin));
	return getText(res);
}

int main(int argc, char** argv){
	ofstream out;
    	out.open("../Database",ios::out | ios::trunc);
	string f_name = "";
	
	string name = "", versions = "", sh_desc = "", desc = "", params = "", return_val = "",
		examples = "", examples_res = "", errors = "";
	string str = "";
	fstream in;
    	stringstream ss;

	string myname = "";
	name = string(argv[0]);
	myname.append(name, 2, name.length() - 2);
	name = "";
	
	if (argc == 1){
		DIR *dir = opendir(".");
		if(dir){
			struct dirent *ent;
			int i = 0;
			DIR *pDir = opendir ("../PHP/");

    			if (pDir == NULL)
    			{
				system("mkdir ../PHP/");
			}
			string req;
			while((ent = readdir(dir)) != NULL){
				f_name = string(ent->d_name);
				if (f_name == myname) continue;
				if (f_name == ".") continue;
				if (f_name == "..") continue;

				//files += string(ent->d_name) + "\n";
				// work with file
				in.open(f_name.c_str(), ios::in);
    				ss << in.rdbuf();
    				in.close();
    				str = ss.str();
				ss.str().resize(0); // савмый стопудовый способ 
				ss.str().clear();
				ss.str( "" );

				name = getName(str);
				//cout << "---- NAME -----" << endl;
				//cout << name << endl;
				//cout << "---- NAME -----" << endl;

				versions = getVer(str);
				if (versions.find("PHP",0) == std::string::npos) continue;
				i++;

				//req = "cp " + f_name + " ../PHP/" + f_name;
				//system(req.data() ); // cp this file to PHP dir

				//cout << "---- VERSIONS -----" << endl;
				//cout << versions << endl;
				//cout << "---- VERSIONS -----" << endl;

				sh_desc = getSh_desc(str);

				//cout << "---- SH-DESC -----" << endl;
				//cout << sh_desc << endl;
				//cout << "---- SH-DESC -----" << endl;

				desc = getDesc(str);
		
				//cout << "---- DESC -----" << endl;
				//cout << desc << endl;
				//cout << "---- DESC -----" << endl;

				params = getParams(str);
		
				//cout << "---- PARAMS -----" << endl;
				//cout << params << endl;
				//cout << "---- PARAMS -----" << endl;

				return_val = getRet_val(str);
		
				//cout << "---- RETURN -----" << endl;
				//cout << return_val << endl;
				//cout << "---- RETURN -----" << endl;
		
						
				errors = getErrors(str);
		
				//cout << "---- ERRORS -----" << endl;
				//cout << errors << endl;
				//cout << "---- ERRORS -----" << endl;
				
				examples = getExamples(str);
		
				//cout << "---- EXAMPLES -----" << endl;
				//cout << examples << endl;
				//cout << "---- EXAMPLES -----" << endl;
		
				examples_res = getExamples_Res(str);
		
				//cout << "---- EXAMPLES-RES -----" << endl;
				//cout << examples_res << endl;
				//cout << "---- EXAMPLES-RES -----" << endl;
				
				string object = "";
				char buff[32];;
				sprintf(buff,"%d",i);
				object += "<object id=" + string(buff) + "> \n";
				object += "<name>" + name + "</name>\n";
				object += "<versions>" + versions + "</versions>\n";
				object += "<short-description>" + sh_desc + "</short-description>\n";
				object += "<description>" + desc + "</description>\n";
				object += "<parameters>" + params + "</parameters>\n";
				object += "<output>" + return_val + "</output>\n";
				object += "<errors>" + errors + "</errors>\n";
				object += "<example>" + examples + "</example>\n";
				object += "<example_result>" + examples_res + "</example_result>\n";
				object += "</object>\n\n";
				out << object;
				out.flush();
			}
			out.close();
		}
		else{
			fprintf(stderr, "Error opening directory\n");
		}
	}
	
	for(int i = 1; i < argc; i++){
		f_name.assign(argv[i]);
		//cout << "file name = " << f_name << endl;

		in.open(f_name.c_str(), ios::in);
    		ss << in.rdbuf();
    		in.close();
    		str = ss.str();
		name = getName(str);
		
		cout << "---- NAME -----" << endl;
		cout << name << endl;
		cout << "---- NAME -----" << endl;

		versions = getVer(str);
		
		cout << "---- VERSIONS -----" << endl;
		cout << versions << endl;
		cout << "---- VERSIONS -----" << endl;

		sh_desc = getSh_desc(str);

		cout << "---- SH-DESC -----" << endl;
		cout << sh_desc << endl;
		cout << "---- SH-DESC -----" << endl;

		desc = getDesc(str);
		
		cout << "---- DESC -----" << endl;
		cout << desc << endl;
		cout << "---- DESC -----" << endl;

		params = getParams(str);
		
		cout << "---- PARAMS -----" << endl;
		cout << params << endl;
		cout << "---- PARAMS -----" << endl;

		return_val = getRet_val(str);
		
		cout << "---- RETURN -----" << endl;
		cout << return_val << endl;
		cout << "---- RETURN -----" << endl;
		
		errors = getErrors(str);
		
		cout << "---- ERRORS -----" << endl;
		cout << errors << endl;
		cout << "---- ERRORS -----" << endl;
		
		examples = getExamples(str);
		
		cout << "---- EXAMPLES -----" << endl;
		cout << examples << endl;
		cout << "---- EXAMPLES -----" << endl;
		
		examples_res = getExamples_Res(str);
		
		cout << "---- EXAMPLES-RES -----" << endl;
		cout << examples_res << endl;
		cout << "---- EXAMPLES-RES -----" << endl;
		
		string object = "";
		char buff[32];;
		int id = 3;
		sprintf(buff,"%d",id);
		object += "<object id=" + string(buff) + "> \n";
		object += "<name>" + name + "</name>\n";
		object += "<versions>" + versions + "</versions>\n";
		object += "<short-description>" + sh_desc + "</short-description>\n";
		object += "<description>" + desc + "</description>\n";
		object += "<parameters>" + params + "</parameters>\n";
		object += "<output>" + return_val + "</output>\n";
		object += "<errors>" + errors + "</errors>\n";
		object += "<example>" + examples + "</example>\n";
		object += "<example_result>" + examples_res + "</example_result>\n";
		object += "</object>\n\n";

		cout << "---- OBJECT ----" << endl;
		cout << object << endl;
	}

	return 0;
}
