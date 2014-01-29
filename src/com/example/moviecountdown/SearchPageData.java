package com.example.moviecountdown;

public class SearchPageData {
	public int current_page;
	public int total_pages;
	
	void SetCurrentPage(int cp) {
		current_page = cp;
	}
	
	int GetCurrentPage() {
		return current_page;
	}
	
	void SetTotalPages(int tp) {
		total_pages = tp;
		
	}
	
	int GetTotalPages() {
		return total_pages;
	}
}
