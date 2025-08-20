package com.example.backend.specification;


import com.example.backend.entity.posts.FarmerPost;
import com.example.backend.entity.posts.PostStatus;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class FarmerPostSpecification {

    public static Specification<FarmerPost> cropNameContains(String cropName) {
        return (root, query, cb) ->
                cropName == null ? null :
                        cb.like(cb.lower(root.get("cropName")), "%" + cropName.toLowerCase() + "%");
    }

    public static Specification<FarmerPost> cropTypeEquals(String cropType) {
        return (root, query, cb) ->
                cropType == null ? null :
                        cb.equal(cb.lower(root.get("cropType")), cropType.toLowerCase());
    }

    public static Specification<FarmerPost> divisionEquals(String division) {
        return (root, query, cb) ->
                division == null ? null :
                        cb.equal(cb.lower(root.get("division")), division.toLowerCase());
    }

    public static Specification<FarmerPost> districtEquals(String district) {
        return (root, query, cb) ->
                district == null ? null :
                        cb.equal(cb.lower(root.get("district")), district.toLowerCase());
    }

    public static Specification<FarmerPost> upazilaEquals(String upazila) {
        return (root, query, cb) ->
                upazila == null ? null :
                        cb.equal(cb.lower(root.get("upazila")), upazila.toLowerCase());
    }

//    public static Specification<FarmerPost> priceBetween(BigDecimal min, BigDecimal max) {
//        return (root, query, cb) -> {
//            if (min == null && max == null) return null;
//            if (min != null && max != null)
//                return cb.between(root.get("pricePerUnit"), min, max);
//            if (min != null)
//                return cb.greaterThanOrEqualTo(root.get("pricePerUnit"), min);
//            return cb.lessThanOrEqualTo(root.get("pricePerUnit"), max);
//        };
//    }

    public static Specification<FarmerPost> statusAvailable() {
        return (root, query, cb) ->
                cb.equal(root.get("status"), PostStatus.AVAILABLE);
    }
}

