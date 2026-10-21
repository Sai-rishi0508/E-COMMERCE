
package com.rishi.springbootecom.service;

import com.rishi.springbootecom.model.Product;
import com.rishi.springbootecom.repo.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepo repo;

    public List<Product> getAllProducts() {
        return repo.findAll();
    }

    public Product getProductById(int id) {
        return repo.findById(id).orElse(null);
    }

    public Product addOrUpdateProduct(
            Product product,
            MultipartFile image) throws IOException {

        // Updating an existing product
        if (product.getId() > 0) {
            Product existingProduct = repo.findById(product.getId())
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Product not found: " + product.getId()));

            // Preserve the current image unless a new image is uploaded
            product.setImageName(existingProduct.getImageName());
            product.setImageType(existingProduct.getImageType());
            product.setImageData(existingProduct.getImageData());
        }

        // Replace image data only when a new file is provided
        if (image != null && !image.isEmpty()) {
            product.setImageName(image.getOriginalFilename());
            product.setImageType(image.getContentType());
            product.setImageData(image.getBytes());
        }

        return repo.save(product);
    }

    public void deleteProduct(int id) {
        repo.deleteById(id);
    }

    public List<Product> searchProducts(String keyword) {
        return repo.searchProducts(keyword);
    }
}
