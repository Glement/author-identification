package com.maximsachok.authoridentification.dto;

public class SearchResultDto {
    private AuthorDto authorDto;
    private Double score;

    @Override
    public String toString() {
        return "SearchResult{" +
                "authorDto=" + authorDto +
                ", score=" + score +
                '}';
    }

    public SearchResultDto(){

    }

    public SearchResultDto(AuthorDto authorDto, Double score) {
        this.authorDto = authorDto;
        this.score = score;
    }

    public AuthorDto getAuthorDto() {
        return authorDto;
    }

    public void setAuthorDto(AuthorDto authorDto) {
        this.authorDto = authorDto;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}
