package com.sky.data;

import com.sky.service.DishService;
import com.sky.service.SetmealService;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RagDataLoader implements CommandLineRunner {

    private final DishService dishService;
    private final SetmealService setmealService;

    private final EmbeddingModel embeddingModel;
    private final InMemoryEmbeddingStore<TextSegment> store;

    @Override
    public void run(String... args) {
        loadDish();
        loadSetmeal();
        System.out.println("RAG 知识库已加载完成！");
    }

    private void loadDish() {
        for (Dish dish : dishService.listAll()) {

            String text = "菜品名称：" + dish.getName() +
                    "；描述：" + dish.getDescription() +
                    "；价格：" + dish.getPrice();

            TextSegment segment = TextSegment.from(text);  // ✔ 正确写法

            store.add(
                    embeddingModel.embed(text).content(),
                    segment
            );
        }
    }

    private void loadSetmeal() {
        for (Setmeal setmeal : setmealService.listAll()) {

            String text = "套餐名称：" + setmeal.getName() +
                    "；描述：" + setmeal.getDescription();

            TextSegment segment = TextSegment.from(text);  // ✔ 正确写法

            store.add(
                    embeddingModel.embed(text).content(),
                    segment
            );
        }
    }
}
