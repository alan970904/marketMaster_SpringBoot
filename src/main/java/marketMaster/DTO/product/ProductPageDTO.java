package marketMaster.DTO.product;

import java.util.List;

import org.springframework.data.domain.Page;

import marketMaster.bean.product.ProductBean;

public class ProductPageDTO {
    private List<ProductBean> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;

    // Constructor
    public ProductPageDTO(Page<ProductBean> page) {
        this.content = page.getContent();
        this.pageNumber = page.getNumber();
        this.pageSize = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
    }

    // Getters and setters
    public List<ProductBean> getContent() {
        return content;
    }

    public void setContent(List<ProductBean> content) {
        this.content = content;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}