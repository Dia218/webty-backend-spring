package org.team14.webty.review.controller;

import org.springframework.stereotype.Controller;
import org.team14.webty.review.service.ReviewService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ReviewController {
	private final ReviewService reviewService;
}
