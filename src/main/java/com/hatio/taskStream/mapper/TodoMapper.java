//package com.hatio.taskStream.mapper;
//
//import com.hatio.taskStream.dto.TodoRequestDTO;
//import com.hatio.taskStream.dto.TodoResponseDTO;
//import com.hatio.taskStream.model.Todo;
//import org.mapstruct.Mapper;
//import org.mapstruct.MappingConstants;
//import org.mapstruct.factory.Mappers;
//
//import java.util.List;
//
//@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
//public interface TodoMapper {
//
//    TodoMapper INSTANCE = Mappers.getMapper(TodoMapper.class);
//
////    @Mapping(target = "createdDate", source = "createdDate", dateFormat = "yyyy-MM-dd'T'HH:mm:ss")
////    @Mapping(target = "updatedDate", source = "updatedDate", dateFormat = "yyyy-MM-dd'T'HH:mm:ss")
//    TodoResponseDTO toTodoResponseDTO(Todo todo);
//
////    @Mapping(target = "createdDate", expression = "java(java.time.LocalDateTime.now())")
////    @Mapping(target = "updatedDate", ignore = true)
//    Todo toTodo(TodoRequestDTO todoRequestDTO);
//
//    List<TodoResponseDTO> toTodoResponseDTOList(List<Todo> todos);
//
//    List<Todo> toTodoList(List<TodoRequestDTO> todoRequestDTOs);
//}