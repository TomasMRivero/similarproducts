package com.tmartinez.similarproducts.infrastructure.adapter.in;

import com.tmartinez.similarproducts.application.exception.ExternalApiNotFoundException;
import com.tmartinez.similarproducts.application.exception.ExternalApiTimeoutException;
import com.tmartinez.similarproducts.application.port.in.GetSimilarProductDetailListUseCase;
import com.tmartinez.similarproducts.domain.model.Product;
import com.tmartinez.similarproducts.infrastructure.adapter.in.api.dto.ProductDetail;
import com.tmartinez.similarproducts.infrastructure.adapter.in.mapper.ProductDetailMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/product")
public class ProductController {
    private final GetSimilarProductDetailListUseCase getProductsService;
    private final ProductDetailMapper productDetailMapper;

    public ProductController(GetSimilarProductDetailListUseCase getProductsService, ProductDetailMapper productDetailMapper) {
        this.getProductsService = getProductsService;
        this.productDetailMapper = productDetailMapper;
    }

    @GetMapping("/{idProduct}/similar")
    public ResponseEntity<List<ProductDetail>> getSimilarProducts(@PathVariable String idProduct){
        try{
            List<Product> response = getProductsService.getSimilarProductDetailList(idProduct);

            if(Objects.isNull(response) || response.isEmpty()){
                return ResponseEntity.notFound().build();
            }

            List<ProductDetail> dto = productDetailMapper.toInDTO(response);

            return ResponseEntity.ok(dto);
        } catch (ExternalApiNotFoundException ex) {
            return ResponseEntity.notFound().build();
        } catch (ExternalApiTimeoutException ex) {
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).build();
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
