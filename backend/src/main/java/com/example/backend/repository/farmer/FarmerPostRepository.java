package com.example.backend.repository.farmer;


import com.example.backend.entity.auth.User;
import com.example.backend.entity.posts.FarmerPost;
import com.example.backend.entity.posts.PostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface FarmerPostRepository extends JpaRepository<FarmerPost, Long>, JpaSpecificationExecutor<FarmerPost> {

    List<FarmerPost> findAllByUser(User user);

    Page<FarmerPost> findByStatus(PostStatus status, Pageable pageable);

    List<FarmerPost> findAllByUser_Id(Long userId);

    void deleteByIdAndUser_Id(Long postId, Long userId);

}
