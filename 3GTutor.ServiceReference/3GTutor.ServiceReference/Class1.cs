using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace WebApplication1
{
    public class Class1
    {
				model.set("id", Integer.parseInt(json.get("id").toString());
				model.set("imageUrl", json.get("ImageUrl").toString();
				model.set("image", BitmapFactory.decodeResource(
						getResources(), R.drawable.ic_launcher);
				String titletemp = json.get("name").toString(;
				if (titletemp.length() > 8) {
					titletemp = titletemp.substring(0, 8) + "....";
				}
				model.set("short_title", titletemp;
				model.set("title", json.get("name").toString();
				model.set("size", json.get("size").toString() + "MB";
				String versiontemp = json.get("version").toString(;
				if (versiontemp.length() > 8) {
					versiontemp = versiontemp.substring(0, 8) + "....";
				}
				model.set("short_version", "V" + versiontemp;
				model.set("version", "V" + json.get("version").toString();
				String introuduceTemp = json.get("Introudution").toString(;
				if (introuduceTemp.length() > 12) {
					introuduceTemp = introuduceTemp.substring(0, 12)
							+ "....";
				}
				model.set("short_description", introuduceTemp;
				model.set("description", json.get("Introudution").toString();
				model.set("recommend _Decrible",
						json.get("recommend _Decrible").toString();
				model.set("DownloadUrl", json.get("DownloadUrl").toString();
    }
}