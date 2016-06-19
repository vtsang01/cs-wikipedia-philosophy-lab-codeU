package com.flatironschool.javacs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import org.jsoup.select.Elements;

public class WikiPhilosophy {
	
	private static WikiFetcher wf = new WikiFetcher();
        final static String WIKI_BASE_URL = "https://en.wikipedia.org"; 
	private static List<String> urls = new ArrayList<String>();
	/**
	 * Tests a conjecture about Wikipedia and Philosophy.
	 * 
	 * https://en.wikipedia.org/wiki/Wikipedia:Getting_to_Philosophy
	 * 
	 * 1. Clicking on the first non-parenthesized, non-italicized link
         * 2. Ignoring external links, links to the current page, or red links
         * 3. Stopping when reaching "Philosophy", a page with no links or a page
         *    that does not exist, or when a loop occurs
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		
        // some example code to get you started

                // String url = "https://en.wikipedia.org/wiki/Java_(programming_language)";
		String url = "https://en.wikipedia.org/wiki/Apple";
		Boolean found = crawl(url); 
                if(found){
                        printURLS(); 
                        System.exit(0);
                }
                else
                        System.exit(1);
	}
        private static Boolean crawl(String url){
                String wiki = "https://en.wikipedia.org/wiki/Philosophy"; 
                // Is it a good idea to make List a global and store links there? 
                urls.add(url); 
                if (url.equals(wiki)){
                        return true; 
                }
                try{
                        //Future: run in parallel w/ multiple thread (async threads ajax)
                        Elements paragraphs = wf.fetchWikipedia(url);
                        for (Element paragraph: paragraphs){
                                Iterable<Node> iter = new WikiNodeIterable(paragraph);
                                int parenthesis_count = 0; 
                                // put into own function
                                for (Node node: iter){
                                        if (node instanceof TextNode) {
                                                TextNode current = (TextNode)node; 
                                                String text = current.text();  
                                                if (text.contains( "(" )){
                                                        parenthesis_count++; 
                                                }
                                                else if (text.contains( ")" )){
                                                        parenthesis_count--; 
                                                }
                                        } 
                                        else if (node instanceof Element){
                                                String link = node.attr("href");
                                                String thresholdText = "/wiki/";
                                                Element elem = (Element)node; 
                                                if (isValidLink(elem) && parenthesis_count == 0){
                                                        String first_url = WIKI_BASE_URL + link;
                                                        return crawl(first_url);
                                                }
                                        }
                                }
                        }
                        return false; 
                }catch(IOException e){
                        e.printStackTrace();
                }
                return false;
               
        }
        private static void printURLS(){
                Iterator<String> urlIter = urls.iterator();
                while (urlIter.hasNext()){
                        System.out.println(urlIter.next());
                }
        }
        private static Boolean isValidLink(Element elem){
                String link = elem.attr("href");
                Elements parents = elem.parents(); 

                return isLinkCurrentPage(link) && notItalics(parents);         
        }

        private static Boolean isLinkCurrentPage(String link){
                String thresholdText = "/wiki/";
                return link.startsWith(thresholdText); 
        }

        private static Boolean notItalics(Elements parents){
                for (Element parent: parents){
                        String tag = parent.tagName(); 
                        if (tag == "i" || tag == "em"){
                                return false; 
                        }
                }
                return true; 
        }
}
