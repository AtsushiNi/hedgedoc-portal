package com.atsushini.hedgedocportal.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.atsushini.hedgedocportal.dto.CurrentUserDto;
import com.atsushini.hedgedocportal.dto.FolderDto;
import com.atsushini.hedgedocportal.dto.RuleDto;
import com.atsushini.hedgedocportal.entity.Rule;
import com.atsushini.hedgedocportal.repository.RuleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RuleService {
    
    private final RuleRepository ruleRepository;

    public List<RuleDto> getRules(CurrentUserDto userDto) {
        List<Rule> ruleDtos = ruleRepository.findByUserId(userDto.getId());

        return ruleDtos.stream().map(this::convertToDto).toList();
    }

    private RuleDto convertToDto(Rule rule) {
        RuleDto dto = new RuleDto();
        dto.setId(rule.getId());
        dto.setTitle(rule.getTitle());
        dto.setRegularExpression(rule.getRegularExpression());

        FolderDto folder = new FolderDto();
        folder.setId(rule.getFolder().getId());
        folder.setTitle(rule.getFolder().getTitle());
        dto.setFolder(folder);

        return dto;
    }
}
