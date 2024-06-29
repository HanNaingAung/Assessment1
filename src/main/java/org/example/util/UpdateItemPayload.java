package org.example.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.model.Item;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateItemPayload {
    private Long id;
    private Item item;
}
