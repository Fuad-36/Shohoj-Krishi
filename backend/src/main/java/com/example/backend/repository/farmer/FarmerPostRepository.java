package com.example.backend.repository.farmer;


import com.example.backend.entity.auth.User;
import com.example.backend.entity.posts.FarmerPost;
import com.example.backend.entity.posts.PostStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FarmerPostRepository extends JpaRepository<FarmerPost, Long> {

    List<FarmerPost> findAllByUser(User user);

    List<FarmerPost> findAllByStatus(PostStatus status);

    List<FarmerPost> findAllByUser_Id(Long userId);

    void deleteByIdAndUser_Id(Long postId, Long userId);

}
