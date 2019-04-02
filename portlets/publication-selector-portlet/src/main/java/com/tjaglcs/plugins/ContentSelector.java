package com.tjaglcs.plugins;

import com.tjaglcs.plugins.Article;

import javax.portlet.RenderRequest;
import javax.portlet.PortletPreferences;
import javax.servlet.http.HttpServletRequest;
import com.liferay.portal.util.PortalUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;



/**
 * Portlet implementation class ContentSelector
 */
public class ContentSelector extends MVCPortlet {
	
	private RenderRequest globalReq;
	private Volume[] volumes;
	
	
	
	
	public Volume[] getVolumes() {
		return volumes;
	}

	public void setVolumes(RenderRequest req) throws Exception {
		
		String volumeConfig = getArticleIdsConfig(req);
		String[] volumeConfigStrings = volumeConfig.split(";");
		
		Volume[] volumeConfigs = new Volume[volumeConfigStrings.length];
		
		for(int i = 0; i<volumeConfigStrings.length; i++) {
			//using -1 to skip the last item, which is archive link
			String volumeString = volumeConfigStrings[i];
			
			System.out.println("volumeString: " + volumeString);
			QueryString queryString = new QueryString(volumeString);
			System.out.println("queryString: " + queryString.getQueryString());
			
			long[] articleIds = queryString.getArticleIds();
			int volumeNumber = queryString.getVolumeNumber();
			
			//System.out.println("article IDs from main: " + articleIds.toString());
			
			
			Volume volume = new Volume(articleIds,volumeNumber,queryString);
			volumeConfigs[i] = volume;
			//System.out.println("ADDING volume " + volume.getVolumeNumber());
		} 
		
		//update class variables for later use
		this.volumes = volumeConfigs;
		this.globalReq = req;

	}
	
	//need a fetch current volume, similar to how I used fetch current article id
	public Volume fetchCurrentVolume(RenderRequest req) {
		System.out.println("vols: " + volumes.length);
		
		//for(int i=0; i<volumes.length; i++) {
		//	System.out.println("volume number: " + volumes[i].getVolumeNumber());
		//}
		
		String queryStringValue = getQueryStringValue("vol");
		System.out.println("string: " + queryStringValue);
		
		
		if(volumes.length==0) {
			//if there are no articles in the config, return an error and prevent from crashing
			System.out.println("no article in config. Please add using the article ID, volume, and issue number (separated by semi-colons): articleId=25147&vol=225&no=4;articleId=25167&vol=225&no=3.");
			//TO DO: need to make this configurable
			return null;
		} else if(queryStringValue==null) {
			//this is if there's no query string, so
			//return most recent
			System.out.println("No article in query string. Using most recent.");
			return volumes[0];
		} //else if(!isArticleListed) {
			//if the query string doesn't match what's in the config, show a not found
			//don't really intend for this to be able to view any article in the database
			//long articleNotFound = getEmptyArticleIdConfig();
			//System.out.println("Query string doesn't match. Article not found. Using: " + articleNotFound);
			//return articleNotFound;
		//} 
	else if(queryStringValue=="browseArchive" || queryStringValue=="selectAnIssue") {
			return null;
		} else {
			System.out.println("Fetching volume: " + queryStringValue);
			System.out.println("total volumes: " + this.volumes.length);
			//Volume currentVolume = objects.stream().filter(v -> v.)
			
			Volume currentVolume = null;
			
			for(int i=0; i<this.volumes.length; i++) {
				System.out.print("i: " + i);
				System.out.print("volume: " + volumes[i].getVolumeNumber());
				
				if(this.volumes[i].getVolumeNumber()==Integer.parseInt(queryStringValue)) {
					System.out.println("found volume " + volumes[i].getVolumeNumber());
					currentVolume = volumes[i];
				} else {
					System.out.println("didn't find volume " + volumes[i].getVolumeNumber());
				}
				
				//if(this.volumes[i].getVolumeNumber()==Integer.parseInt(queryStringValue)) {
				//	return this.volumes[i];
				//} else {
				//	System.out.println("volume not found");
				//	return null;
				//}
			}
			
			//return Long.parseLong(articleIdFromString);
			return currentVolume;
		}
		
		
		
	}
	
	
	
	
//	public Article[] getArticleObjs(RenderRequest req) throws Exception {
//		//method to build an array of article objects based on the portlet config
//		//objects will be used to populate dropdown, check against for query string, and select article
//		
//		String articleConfig = getArticleIdsConfig(req);
//		String[] articleConfigStrings = articleConfig.split(";");
//		
//		Article[] articleConfigs = new Article[articleConfigStrings.length];
//		
//		for(int i = 0; i<articleConfigStrings.length; i++) {
//			//using -1 to skip the last item, which is archive link
//			String articleString = articleConfigStrings[i];
//		
//			long articleId = Long.parseLong(extractQueryStringVals(articleString,"articleId"));
//			int volume = Integer.parseInt(extractQueryStringVals(articleString,"vol"));
//			int issue = Integer.parseInt(extractQueryStringVals(articleString,"no"));
//			
//			Article article = new Article(articleId,volume,issue);
//			articleConfigs[i] = article;
//		} 
//		
//		//update class variables for later use
//		this.articles = articleConfigs;
//		this.globalReq = req;
//		
//		return articleConfigs;
//
//	}
	
//	public Long fetchCurrentArticleId(RenderRequest req) throws Exception{
//		//method to determin which article will be displayed
//		//based on query string and list of articles in config
//		
//		String articleIdFromString = getQueryStringValue("articleId");
//		
//		//does the article from the query string match the articles in the config?
//		boolean isArticleListed = checkArticleList(articles, articleIdFromString);
//		
//		System.out.println("art length: " + articles[0].getQueryString());
//
//		if(articles[0].getArticleId()==-1) {
//			//if there are no articles in the config, return an error and prevent from crashing
//			System.out.println("no article in config. Please add using the article ID, volume, and issue number (separated by semi-colons): articleId=25147&vol=225&no=4;articleId=25167&vol=225&no=3.");
//			//TO DO: need to make this configurable
//			return 0L;
//		} else if(articleIdFromString==null) {
//			//this is if there's no query string, so
//			//return most recent
//			System.out.println("No article in query string. Using most recent.");
//			return articles[0].getArticleId();
//		} else if(!isArticleListed) {
//			//if the query string doesn't match what's in the config, show a not found
//			//don't really intend for this to be able to view any article in the database
//			long articleNotFound = getEmptyArticleIdConfig();
//			System.out.println("Query string doesn't match. Article not found. Using: " + articleNotFound);
//			return articleNotFound;
//		} else if(articleIdFromString=="browseArchive" || articleIdFromString=="selectAnIssue") {
//			return 0L;
//		} else {
//			System.out.println("Fetching article from query string: " + articleIdFromString);
//			return Long.parseLong(articleIdFromString);
//		}
//
//	}
//	
// 
//	public boolean isMostRecent() throws Exception {
//		long articleId = fetchCurrentArticleId(globalReq);
//		long mostRecentArticle = articles[0].getArticleId();
//		
//		if(mostRecentArticle==articleId) {
//			return true;
//		} else {
//			return false;
//		}
//		
//	}
	
	private String getQueryStringValue(String stringParam) {
		HttpServletRequest httpReq = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(globalReq));
		String queryString = httpReq.getParameter(stringParam);
		System.out.println("queryString: " + queryString);
		return queryString;
	}
	
	public long getGroupId(RenderRequest req) {
		ThemeDisplay themeDisplay = getThemeDisplay(req);
		long portletGroupId = themeDisplay.getScopeGroupId();
		
		return portletGroupId;
	}
	
	private ThemeDisplay getThemeDisplay(RenderRequest req) {
		return (ThemeDisplay) req.getAttribute(WebKeys.THEME_DISPLAY);
	}
	
	
	private String getArticleIdsConfig(RenderRequest req) throws Exception {
		PortletPreferences portletPreferences = req.getPreferences();
		String articleIDs = GetterUtil.getString(portletPreferences.getValue("contentSelectorIncludeArticles", "-1"));
		
		return articleIDs;
	}
//	
//	private long getEmptyArticleIdConfig() throws Exception {
//		PortletPreferences portletPreferences = globalReq.getPreferences();
//		String emptyArticleString = GetterUtil.getString(portletPreferences.getValue("contentSelectorArticleNotFound", "-1"));
//		//System.out.println("emptyArticleString: " + emptyArticleString);
//		
//		long emptyArticle;
//		
//		try {
//			emptyArticle = Long.parseLong(emptyArticleString);
//		} catch (Exception e) {
//			emptyArticle=-1;
//			e.printStackTrace();
//		}
//		
//		//System.out.println("emptyArticle: " + emptyArticle);
//		return emptyArticle;
//	}
//
//	public String getArchiveUrl() {
//		PortletPreferences portletPreferences = globalReq.getPreferences();
//		String archiveUrlString = GetterUtil.getString(portletPreferences.getValue("contentSelectorArchiveUrl", "https://tjaglcspublic.army.mil/mlr-archives"));
//		return archiveUrlString;
//	}
//	
//	
//	public String fetchArticleList() {
//		String articleList = "";
//		
//		for(int i=0; i<articles.length; i++) {
//			String currentArticle = articles[i].getArticleId() + ";"; 
//			articleList += currentArticle;
//		}
//		
//		return articleList;
//	}
//	
//	private boolean checkArticleList(Article[] articles, String queryString) {
//		//method to check if articleId from query string exists in list of articles in portlet
//		
//		boolean isArticleListed = false;
//		
//		if(queryString==null) {
//			return false;
//		}
//		
//		for(int i = 0; i<articles.length; i++) {
//			
//			try {
//				Long.parseLong(queryString);
//			} catch (NumberFormatException e) {
//				// TODO Auto-generated catch block
//				System.out.print("Invalid query string");
//				e.printStackTrace();
//				continue;
//			}
//			
//			if(articles[i].getArticleId()==Long.parseLong(queryString)) {
//				isArticleListed=true;
//				
//				break;
//			}
//
//		} 
//		
//		return isArticleListed;
//		
//	}

	
}
