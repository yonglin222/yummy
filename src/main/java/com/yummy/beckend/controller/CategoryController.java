// package com.yummy.beckend.controller;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;
 
// import com.yummy.beckend.dto.RecipeDto;
// import com.yummy.beckend.dto.CategoryDto;
// import com.yummy.beckend.service.RecipeService;
// import com.yummy.beckend.service.CategoryService;

// import io.swagger.v3.oas.annotations.Operation;
// import io.swagger.v3.oas.annotations.Parameter;
// import io.swagger.v3.oas.annotations.tags.Tag;

// import java.sql.SQLException;
// import java.util.List;

// @Tag(name = "5. 카테고리", description = "종류별/방법별 레시피 분류 조회")
// @Controller
// @RequestMapping("/category") 
// public class CategoryController {

//     @Autowired
//     private RecipeService recipeService; 
    
//     @Autowired
//     private CategoryService categoryService;

//     private Long safeParseLong(String idString) {
//         if (idString == null || idString.equals("undefined") || idString.equals("null") || idString.isEmpty()) {
//             return 0L; 
//         }
//         try {
//             return Long.valueOf(idString);
//         } catch (NumberFormatException e) {
//             return 0L; 
//         }
//     }

//     @Operation(summary = "카테고리별 레시피 조회", description = "종류별(type) 및 조리법별(method) 필터를 적용하여 레시피를 조회합니다.")
//     @GetMapping({"", "/"}) 
//     public String categoryPage(
//         @Parameter(description = "종류 카테고리 ID (0이면 전체)") @RequestParam(value = "typeCatId", defaultValue = "0", required = false) String typeCatIdStr,
//         @Parameter(description = "방법 카테고리 ID (0이면 전체)") @RequestParam(value = "methodCatId", defaultValue = "0", required = false) String methodCatIdStr,
//         @Parameter(hidden = true) Model model) throws SQLException {
        
//         Long typeCatId = safeParseLong(typeCatIdStr);
//         Long methodCatId = safeParseLong(methodCatIdStr);

//         List<RecipeDto> recipeList;

//         if (typeCatId != 0 || methodCatId != 0) {
//             recipeList = categoryService.getRecipesByTypeAndMethod(typeCatId, methodCatId);
//         } else {
//             recipeList = recipeService.getAllRecipes();
//         }

//         List<CategoryDto> typeCategories = categoryService.getAllTypeCategories();
//         List<CategoryDto> methodCategories = categoryService.getAllMethodCategories();

//         model.addAttribute("typeCategories", typeCategories);
//         model.addAttribute("methodCategories", methodCategories);
        
//         model.addAttribute("selectedTypeCatId", typeCatId);
//         model.addAttribute("selectedMethodCatId", methodCatId);
        
//         model.addAttribute("recipeList", recipeList);
        
//         return "category/list"; 
//     }
// }